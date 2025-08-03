import { Hono } from "hono";
import { NetworkEnum } from "@1inch/fusion-sdk";
import { FusionResolver, ResolverConfig } from "../utils/fusionResolver";

const app = new Hono();

// Global resolver instance
let resolverInstance: FusionResolver | null = null;

// Helper function to convert string to NetworkEnum
function getNetworkEnum(networkStr: string): NetworkEnum {
  switch (networkStr.toLowerCase()) {
    case "ethereum":
      return NetworkEnum.ETHEREUM;
    case "binance":
    case "bsc":
    case "bnb":
      return NetworkEnum.BINANCE;
    case "polygon":
      return NetworkEnum.POLYGON;
    case "arbitrum":
      return NetworkEnum.ARBITRUM;
    case "optimism":
      return NetworkEnum.OPTIMISM;
    default:
      throw new Error(`Unsupported network: ${networkStr}`);
  }
}

// Start the Fusion resolver
app.post("/start", async (c) => {
  try {
    const body = await c.req.json();
    
    const config: ResolverConfig = {
      network: getNetworkEnum(body.network || "ethereum"),
      minProfitBps: body.minProfitBps || 50,
      apiKey: process.env.ONEINCH_API_KEY || "",
      nodeUrl: "" // This will be determined by getRpcUrl() in the resolver
    };

    resolverInstance = new FusionResolver(config);
    await resolverInstance.startResolving();

    return c.json({
      success: true,
      message: "Fusion resolver started",
      config: {
        network: body.network,
        minProfitBps: config.minProfitBps
      }
    });
  } catch (error) {
    console.error("Failed to start resolver:", error);
    return c.json({
      error: "Failed to start resolver",
      details: error instanceof Error ? error.message : "Unknown error"
    }, 500);
  }
});

// Stop the Fusion resolver
app.post("/stop", async (c) => {
  try {
    if (!resolverInstance || !resolverInstance.getStatus().isListening) {
      return c.json({ error: "Resolver is not running" }, 400);
    }

    resolverInstance.stopResolving();
    resolverInstance = null;

    return c.json({
      success: true,
      message: "Fusion resolver stopped"
    });
  } catch (error) {
    console.error("Error stopping resolver:", error);
    return c.json({ 
      error: "Failed to stop resolver", 
      details: error instanceof Error ? error.message : "Unknown error" 
    }, 500);
  }
});

// Get resolver status and stats
app.get("/status", async (c) => {
  try {
    if (!resolverInstance) {
      return c.json({
        isRunning: false,
        message: "Resolver not initialized"
      });
    }

    const status = resolverInstance.getStatus();
    
    return c.json({
      isRunning: status.isListening,
      network: status.network,
      config: {
        network: status.config.network,
        minProfitBps: status.config.minProfitBps
      },
      message: status.isListening ? "Resolver is actively listening for orders" : "Resolver is stopped"
    });
  } catch (error) {
    console.error("Error getting resolver status:", error);
    return c.json({ 
      error: "Failed to get resolver status", 
      details: error instanceof Error ? error.message : "Unknown error" 
    }, 500);
  }
});

// Create a competitive order using SDK
app.post("/create-order", async (c) => {
  try {
    if (!resolverInstance || !resolverInstance.getStatus().isListening) {
      return c.json({ error: "Resolver is not running" }, 400);
    }

    const body = await c.req.json();
    const orderParams = body.params;

    if (!orderParams) {
      return c.json({ error: "Order parameters are required" }, 400);
    }

    const result = await resolverInstance.createCompetitiveOrder(orderParams);

    return c.json({
      success: result.success,
      order: result.order ? JSON.parse(JSON.stringify(result.order, (key, value) =>
        typeof value === 'bigint' ? value.toString() : value
      )) : undefined,
      error: result.error
    });
  } catch (error) {
    console.error("Error creating order:", error);
    return c.json({ 
      error: "Failed to create order", 
      details: error instanceof Error ? error.message : "Unknown error" 
    }, 500);
  }
});

// Submit order to 1inch Fusion using SDK
app.post("/submit-order", async (c) => {
  try {
    if (!resolverInstance || !resolverInstance.getStatus().isListening) {
      return c.json({ error: "Resolver is not running" }, 400);
    }

    const body = await c.req.json();
    
    // Expect the complete response from create-order: { success: true, order: { order: {...}, quoteId: "..." } }
    // OR just the order part: { order: {...}, quoteId: "..." }
    let orderData, quoteId;
    
    if (body.success && body.order) {
      // Complete create-order response
      orderData = body.order.order;
      quoteId = body.order.quoteId;
    } else if (body.order && body.quoteId) {
      // Just the order part
      orderData = body.order;
      quoteId = body.quoteId;
    } else {
      return c.json({ error: "Invalid format. Expected create-order response or {order, quoteId}" }, 400);
    }

    if (!orderData || !quoteId) {
      return c.json({ error: "Order and quoteId are required" }, 400);
    }

    const result = await resolverInstance.submitOrderToFusion(orderData, quoteId);

    return c.json({
      success: result.success,
      orderHash: result.orderHash,
      error: result.error
    });
  } catch (error) {
    console.error("Error submitting order:", error);
    return c.json({ 
      error: "Failed to submit order", 
      details: error instanceof Error ? error.message : "Unknown error" 
    }, 500);
  }
});

// Track order status using SDK
app.get("/track-order/:orderHash", async (c) => {
  try {
    if (!resolverInstance || !resolverInstance.getStatus().isListening) {
      return c.json({ error: "Resolver is not running" }, 400);
    }

    const orderHash = c.req.param("orderHash");

    if (!orderHash) {
      return c.json({ error: "Order hash is required" }, 400);
    }

    const result = await resolverInstance.trackOrderStatus(orderHash);

    return c.json({
      orderHash,
      status: result.status,
      fills: result.fills,
      error: result.error
    });
  } catch (error) {
    console.error("Error tracking order:", error);
    return c.json({ 
      error: "Failed to track order", 
      details: error instanceof Error ? error.message : "Unknown error" 
    }, 500);
  }
});

// Manually fill a specific order (for testing)
app.post("/fill-order", async (c) => {
  try {
    if (!resolverInstance || !resolverInstance.getStatus().isListening) {
      return c.json({ error: "Resolver is not running" }, 400);
    }

    const body = await c.req.json();
    const order = body.order;

    if (!order) {
      return c.json({ error: "Order data is required" }, 400);
    }

    const result = await resolverInstance.fillOrder(order);

    return c.json({
      success: result.success,
      txHash: result.txHash,
      error: result.error
    });
  } catch (error) {
    console.error("Error filling order:", error);
    return c.json({ 
      error: "Failed to fill order", 
      details: error instanceof Error ? error.message : "Unknown error" 
    }, 500);
  }
});

export default app; 