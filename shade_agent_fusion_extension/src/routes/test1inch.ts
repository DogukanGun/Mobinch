import { Hono } from "hono";
import { FusionSDK, NetworkEnum } from "@1inch/fusion-sdk";
import { ethers } from "ethers";

const app = new Hono();

// Test 1inch Fusion SDK basic functionality
app.get("/sdk-test", async (c) => {
  try {
    console.log("🧪 Testing 1inch Fusion SDK integration...");

    // Test 1: Check if SDK can be imported and instantiated
    const ethersProvider = new ethers.JsonRpcProvider("https://eth.llamarpc.com");
    
    const web3Provider = {
      eth: {
        call: (transactionConfig: any): Promise<string> => {
          return ethersProvider.call(transactionConfig);
        }
      },
      extend: (): void => {}
    };

    console.log("✅ Step 1: Ethers provider created successfully");

    // Test 2: Try to create SDK instance (without private key issues)
    let sdkStatus = "❌ Not initialized";
    let sdkError = null;

    try {
      // Create a valid temporary private key for testing
      const privateKey = "0x" + "1".padStart(64, "0");
      
      const { PrivateKeyProviderConnector } = await import("@1inch/fusion-sdk");
      
      const connector = new PrivateKeyProviderConnector(
        privateKey,
        web3Provider
      );

      const sdk = new FusionSDK({
        url: 'https://api.1inch.dev/fusion',
        network: NetworkEnum.ETHEREUM,
        blockchainProvider: connector,
        authKey: process.env.ONEINCH_API_KEY || ""
      });

      sdkStatus = "✅ SDK initialized successfully";
      console.log("✅ Step 2: FusionSDK created successfully");

    } catch (error) {
      sdkError = error instanceof Error ? error.message : "Unknown error";
      console.log("❌ Step 2: FusionSDK creation failed:", sdkError);
    }

    // Test 3: Check network enum and constants
    const networkTest = {
      ethereum: NetworkEnum.ETHEREUM,
      polygon: NetworkEnum.POLYGON,
      arbitrum: NetworkEnum.ARBITRUM,
      bnb: NetworkEnum.BINANCE
    };

    console.log("✅ Step 3: Network enums available:", networkTest);

    return c.json({
      success: true,
      testResults: {
        sdkIntegration: {
          status: sdkStatus,
          error: sdkError
        },
        networkSupport: networkTest,
        timestamp: new Date().toISOString()
      },
      nextSteps: {
        needsApiKey: process.env.ONEINCH_API_KEY ? "✅ API key configured" : "❌ Get 1inch API key from https://portal.1inch.dev/",
        needsAgentDeployment: "Run shade-agent-cli for NEAR Chain Signatures",
        readyForTesting: sdkStatus.includes("✅") && !!process.env.ONEINCH_API_KEY
      }
    });

  } catch (error) {
    console.error("🚨 Test failed:", error);
    return c.json({ 
      success: false,
      error: error instanceof Error ? error.message : "Unknown error",
      details: "Failed to test 1inch integration"
    }, 500);
  }
});

// Test SDK quote functionality (the proper way to test 1inch Fusion API)
app.get("/quote-test", async (c) => {
  try {
    console.log("💰 Testing SDK quote functionality...");

    const apiKey = process.env.ONEINCH_API_KEY;
    if (!apiKey) {
      return c.json({
        success: false,
        error: "No API key found in environment",
        note: "Set ONEINCH_API_KEY in .env.development.local"
      });
    }

    // Create SDK instance
    const ethersProvider = new ethers.JsonRpcProvider("https://eth.llamarpc.com");
    
    const web3Provider = {
      eth: {
        call: (transactionConfig: any): Promise<string> => {
          return ethersProvider.call(transactionConfig);
        }
      },
      extend: (): void => {}
    };

    const privateKey = "0x" + "1".padStart(64, "0");
    const { PrivateKeyProviderConnector } = await import("@1inch/fusion-sdk");
    
    const connector = new PrivateKeyProviderConnector(
      privateKey,
      web3Provider
    );

    const sdk = new FusionSDK({
      url: 'https://api.1inch.dev/fusion',
      network: NetworkEnum.BINANCE, // Change to other networks when necessary
      blockchainProvider: connector,
      authKey: apiKey
    });

               // Test getting a quote using SDK methods 
           const quoteParams = {
             fromTokenAddress: '0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c', // WBNB (Wrapped BNB)
             toTokenAddress: '0x8ac76a51cc950d9822d68b83fe1ad97b32cd580d', // USDC 
             amount: '2000000000000000', // 0.002 WBNB 
             walletAddress: '0x312e5cfdF8847ce9708355e4cBB9b86CF637313c', // TEE wallet with real WBNB
             source: 'test-wbnb-to-usdc-quote'
           };

    console.log("Testing SDK quote with params:", quoteParams);

    let quoteResult = null;
    let quoteError = null;

    try {
      const quote = await sdk.getQuote(quoteParams);
      quoteResult = {
        recommendedPreset: quote.recommendedPreset,
        presets: Object.keys(quote.presets),
        success: true
      };
      console.log("✅ Quote generated successfully:", quote.recommendedPreset);
    } catch (error) {
      quoteError = error instanceof Error ? error.message : "Unknown quote error";
      
      if (error && typeof error === 'object' && 'response' in error) {
        const response = (error as any).response;
        if (response && response.data) {
          quoteError = `API Error: ${JSON.stringify(response.data)}`;
        }
      }
      console.log("❌ Quote generation failed:", quoteError);
    }

    return c.json({
      success: true,
      apiKeyStatus: `${apiKey.substring(0, 10)}... (configured)`,
      quoteTest: {
        result: quoteResult,
        error: quoteError,
        parameters: quoteParams
      },
      message: quoteResult ? "✅ 1inch Fusion SDK working correctly!" : "❌ SDK quote generation failed",
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    return c.json({
      success: false,
      error: error instanceof Error ? error.message : "Unknown error"
    }, 500);
  }
});

// Test the specific Fusion SDK methods
app.get("/resolver-methods-test", async (c) => {
  try {
    console.log("🔧 Testing resolver-specific SDK methods...");

    const apiKey = process.env.ONEINCH_API_KEY;
    if (!apiKey) {
      return c.json({
        success: false,
        error: "No API key found in environment"
      });
    }

    const ethersProvider = new ethers.JsonRpcProvider("https://eth.llamarpc.com");
    
    const web3Provider = {
      eth: {
        call: (transactionConfig: any): Promise<string> => {
          return ethersProvider.call(transactionConfig);
        }
      },
      extend: (): void => {}
    };

    const privateKey = "0x" + "1".padStart(64, "0");
    const { PrivateKeyProviderConnector } = await import("@1inch/fusion-sdk");
    
    const connector = new PrivateKeyProviderConnector(
      privateKey,
      web3Provider
    );

    const sdk = new FusionSDK({
      url: 'https://api.1inch.dev/fusion',
      network: NetworkEnum.ETHEREUM,
      blockchainProvider: connector,
      authKey: apiKey
    });

    const methods = [];

    // Test 1: Quote generation 
    try {
      methods.push({
        method: "getQuote",
        available: typeof sdk.getQuote === 'function',
        description: "Generate price quotes for swaps"
      });
    } catch (e) {
      methods.push({
        method: "getQuote",
        available: false,
        description: "Generate price quotes for swaps",
        error: e instanceof Error ? e.message : "Unknown"
      });
    }

    // Test 2: Order creation
    try {
      methods.push({
        method: "createOrder",
        available: typeof sdk.createOrder === 'function',
        description: "Create Fusion orders"
      });
    } catch (e) {
      methods.push({
        method: "createOrder",
        available: false,
        description: "Create Fusion orders",
        error: e instanceof Error ? e.message : "Unknown"
      });
    }

    // Test 3: Order submission  
    try {
      methods.push({
        method: "submitOrder",
        available: typeof sdk.submitOrder === 'function',
        description: "Submit orders to 1inch"
      });
    } catch (e) {
      methods.push({
        method: "submitOrder",
        available: false,
        description: "Submit orders to 1inch",
        error: e instanceof Error ? e.message : "Unknown"
      });
    }

    // Test 4: Order status tracking
    try {
      methods.push({
        method: "getOrderStatus",
        available: typeof sdk.getOrderStatus === 'function',
        description: "Track order execution status"
      });
    } catch (e) {
      methods.push({
        method: "getOrderStatus",
        available: false,
        description: "Track order execution status",
        error: e instanceof Error ? e.message : "Unknown"
      });
    }

    const allMethodsAvailable = methods.every(m => m.available);

    return c.json({
      success: true,
      resolverReadiness: allMethodsAvailable ? "✅ All SDK methods available" : "❌ Some methods missing",
      methods,
      readyForResolver: allMethodsAvailable,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    return c.json({
      success: false,
      error: error instanceof Error ? error.message : "Unknown error"
    }, 500);
  }
});

// Test complete SDK flow including createOrder
app.get("/complete-flow-test", async (c) => {
  try {
    console.log("🔄 Testing complete 1inch Fusion SDK flow...");

    const apiKey = process.env.ONEINCH_API_KEY;
    if (!apiKey) {
      return c.json({
        success: false,
        error: "No API key found in environment"
      });
    }

    // Create SDK instance 
    const ethersProvider = new ethers.JsonRpcProvider("https://bsc-dataseed.binance.org");
    
    const web3Provider = {
      eth: {
        call: (transactionConfig: any): Promise<string> => {
          return ethersProvider.call(transactionConfig);
        }
      },
      extend: (): void => {}
    };

    const privateKey = "0x" + "1".padStart(64, "0");
    const { PrivateKeyProviderConnector } = await import("@1inch/fusion-sdk");
    
    const connector = new PrivateKeyProviderConnector(
      privateKey,
      web3Provider
    );

    const sdk = new FusionSDK({
      url: 'https://api.1inch.dev/fusion',
      network: NetworkEnum.BINANCE,
      blockchainProvider: connector,
      authKey: apiKey
    });

    // Test parameters 
    const params = {
      fromTokenAddress: '0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c', // WBNB
      toTokenAddress: '0x8ac76a51cc950d9822d68b83fe1ad97b32cd580d', // USDC
      amount: '2000000000000000', // 0.002 WBNB
      walletAddress: '0x312e5cfdF8847ce9708355e4cBB9b86CF637313c', // TEE wallet
      source: 'complete-flow-test'
    };

    console.log("Step 1: Getting quote...");
    const quote = await sdk.getQuote(params);
    
    console.log("Step 2: Creating order with SAME params...");
    const preparedOrder = await sdk.createOrder(params); // Same params as getQuote!
    
    return c.json({
      success: true,
      flow: {
        step1_quote: {
          success: true,
          recommendedPreset: quote.recommendedPreset,
          presets: quote.presets ? Object.keys(quote.presets) : []
        },
        step2_createOrder: {
          success: true,
          hasOrder: !!preparedOrder.order,
          hasQuoteId: !!preparedOrder.quoteId,
          orderType: typeof preparedOrder.order
        }
      },
      message: "✅ Complete SDK flow working!",
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error("Complete flow test error:", error);
    return c.json({
      success: false,
      error: error instanceof Error ? error.message : "Unknown error",
      timestamp: new Date().toISOString()
    }, 500);
  }
});

// Test complete SDK flow including submission
app.get("/complete-submission-test", async (c) => {
  try {
    console.log("🔄 Testing complete 1inch Fusion submission flow...");

    const apiKey = process.env.ONEINCH_API_KEY;
    if (!apiKey) {
      return c.json({
        success: false,
        error: "No API key found in environment"
      });
    }

    // Create SDK instance
    const ethersProvider = new ethers.JsonRpcProvider("https://bsc-dataseed.binance.org");
    
    const web3Provider = {
      eth: {
        call: (transactionConfig: any): Promise<string> => {
          return ethersProvider.call(transactionConfig);
        }
      },
      extend: (): void => {}
    };

    const privateKey = process.env.ETH_PRIVATE_KEY || "0x" + "1".padStart(64, "0"); // gives error without the second part when inserted inside the provider..
    const { PrivateKeyProviderConnector } = await import("@1inch/fusion-sdk");
    
    const connector = new PrivateKeyProviderConnector(
      privateKey,
      web3Provider
    );

    const sdk = new FusionSDK({
      url: 'https://api.1inch.dev/fusion',
      network: NetworkEnum.BINANCE,
      blockchainProvider: connector,
      authKey: apiKey
    });

    // Test parameters
    const params = {
      fromTokenAddress: '0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c', // WBNB
      toTokenAddress: '0x8ac76a51cc950d9822d68b83fe1ad97b32cd580d', // USDC
      amount: '10000000000000000', // 0.002 WBNB
      walletAddress: '0x7D4CD93532c0469AE55Ad7138Df6f20D13F33E9f',
      source: 'complete-flow-test'
    };

    console.log("Step 1: Getting quote...");
    const quote = await sdk.getQuote(params);
    
    console.log("Step 2: Creating order with SAME params...");
    const preparedOrder = await sdk.createOrder(params); // This returns the order instance with .build()
    
    console.log("Step 3: Submitting order to 1inch Fusion...");
    const info = await sdk.submitOrder(preparedOrder.order, preparedOrder.quoteId);
    
    return c.json({
      success: true,
      flow: {
        step1_quote: {
          success: true,
          recommendedPreset: quote.recommendedPreset,
          presets: quote.presets ? Object.keys(quote.presets) : []
        },
        step2_createOrder: {
          success: true,
          hasOrder: !!preparedOrder.order,
          hasQuoteId: !!preparedOrder.quoteId,
          orderType: typeof preparedOrder.order
        },
        step3_submitOrder: {
          success: true,
          orderHash: info.orderHash,
          orderHashType: typeof info.orderHash
        }
      },
      message: "✅ Complete submission flow working!",
      orderHash: info.orderHash,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error("Complete submission test error:", error);
    return c.json({
      success: false,
      error: error instanceof Error ? error.message : "Unknown error",
      timestamp: new Date().toISOString()
    }, 500);
  }
});

export default app; 