const API_URL = "http://127.0.0.1:8080/prices";
const HOLDINGS_API = "http://127.0.0.1:8080/api/holdings";
const REFRESH_INTERVAL = 15000;

const SUMMARY_API =
  "http://127.0.0.1:8080/api/holdings/summary";

async function loadSummary() {
  try {
    
    const res = await fetch(SUMMARY_API);
    const summary = await res.json();

    document.getElementById("stocksStat").innerHTML =
      `₹${(summary.STOCK || 0).toFixed(2)}<span>Stocks</span>`;

    document.getElementById("bondsStat").innerHTML =
      `₹${(summary.BOND || 0).toFixed(2)}<span>Bonds</span>`;

    document.getElementById("cryptoStat").innerHTML =
      `₹${(summary.CRYPTO || 0).toFixed(2)}<span>Crypto</span>`;

    document.getElementById("cashStat").innerHTML =
      `₹${(summary.CASH || 0).toFixed(2)}<span>Cash</span>`;
  } catch (err) {
    console.error("Error loading summary:", err);
  }
}


// Load Holdings from DB
async function loadHoldings() {
  const res = await fetch(HOLDINGS_API);
  const holdings = await res.json();

  const tbody = document.getElementById("holdingsBody");
  tbody.innerHTML = "";

  holdings.forEach(h => {
    const symbol = h.asset.symbol;

    const row = document.createElement("tr");
    row.setAttribute("data-symbol", symbol);

    row.innerHTML = `
      <td>${symbol}</td>
      <td>${h.asset.type}</td>
      <td>${h.quantity}</td>
      <td>${h.avgBuyPrice}</td>
      <td class="current-price">Loading...</td>
      <td class="market-value">Loading...</td>
      <td class="pl">Loading...</td>
    `;

    tbody.appendChild(row);
  });

  updatePrices();
}

// Fetch Prices from Backend
async function updatePrices() {
  const rows = document.querySelectorAll("#holdingsBody tr");
  const symbols = Array.from(rows).map(r => r.getAttribute("data-symbol"));

  if (symbols.length === 0) return;

  const res = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(symbols),
  });

  const data = await res.json();

  rows.forEach(row => {
    const symbol = row.getAttribute("data-symbol");
    const currentPrice = data[symbol]?.price;

    const units = parseFloat(row.children[2].innerText);
    const buyPrice = parseFloat(row.children[3].innerText);

    if (currentPrice) {
      const marketValue = currentPrice * units;
      const pl = marketValue - buyPrice * units;

      row.querySelector(".current-price").innerText = `₹${currentPrice}`;
      row.querySelector(".market-value").innerText = `₹${marketValue.toFixed(2)}`;
      row.querySelector(".pl").innerText = `₹${pl.toFixed(2)}`;
    }
  });
  updateDashboardTotals();
}

function updateDashboardTotals() {
  const totals = {
    STOCK: 0,
    BOND: 0,
    CRYPTO: 0,
    CASH: 0
  };

  const rows = document.querySelectorAll("#holdingsBody tr");

  rows.forEach(row => {
    const type = row.children[1].innerText; // STOCK/CRYPTO...
    const plText = row.querySelector(".pl").innerText.replace("₹", "");
    const pl = parseFloat(plText);

    if (!isNaN(pl)) {
      totals[type] += pl;
    }
  });

  document.getElementById("stocksStat").innerHTML =
    `₹${totals.STOCK.toFixed(2)}<span>Stocks</span>`;

  document.getElementById("bondsStat").innerHTML =
    `₹${totals.BOND.toFixed(2)}<span>Bonds</span>`;

  document.getElementById("cryptoStat").innerHTML =
    `₹${totals.CRYPTO.toFixed(2)}<span>Crypto</span>`;

  document.getElementById("cashStat").innerHTML =
    `₹${totals.CASH.toFixed(2)}<span>Cash</span>`;
}



window.addEventListener("load", () => {
  loadHoldings();
});


// Refresh prices every 15 sec
setInterval(updatePrices, REFRESH_INTERVAL);
