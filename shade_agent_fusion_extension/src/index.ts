import { Hono } from "hono";
import { serve } from "@hono/node-server";
import { cors } from "hono/cors";
import dotenv from "dotenv";

// Load environment variables from .env file (only needed for local development)
if (process.env.NODE_ENV !== "production") {
  dotenv.config({ path: ".env.development.local" });
}

// Import existing routes
import ethAccount from "./routes/ethAccount";
import agentAccount from "./routes/agentAccount";
import transaction from "./routes/transaction";

// Import Fusion resolver route
import fusionResolver from "./routes/fusionResolver";

// Import test route
import test1inch from "./routes/test1inch";

const app = new Hono();

// Configure CORS to restrict access to the server
app.use(cors());

// Health check
app.get("/", (c) => c.json({ 
  message: "NEAR Shade Agent - ETH Oracle & Fusion Resolver",
  version: "1.0.0",
  capabilities: ["eth-price-oracle", "fusion-resolver"],
  timestamp: new Date().toISOString()
}));

// Existing routes (ETH price oracle functionality)
app.route("/api/eth-account", ethAccount);
app.route("/api/agent-account", agentAccount);
app.route("/api/transaction", transaction);

// New Fusion resolver route
app.route("/api/fusion/resolver", fusionResolver);

// Test route for 1inch integration
app.route("/api/test", test1inch);

// Start the server
const port = Number(process.env.PORT || "3000");

console.log(`App is running on port ${port}`);

serve({ fetch: app.fetch, port });
