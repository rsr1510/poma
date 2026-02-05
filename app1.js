const API_URL = "http://127.0.0.1:8000/prices";
const REFRESH_INTERVAL = 15000;

async function updatePrices() {
  const symbols = [];
  const rows = document.querySelectorAll('#holdingsTable tbody tr');
  rows.forEach(row => symbols.push(row.getAttribute('data-symbol')));

  try {
    const res = await fetch(API_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(symbols),
    });

    const data = await res.json();

    rows.forEach(row => {
      const symbol = row.getAttribute('data-symbol');
      const currentPrice = data[symbol]?.price;
      const units = parseFloat(row.cells[2].innerText);
      const buyPrice = parseFloat(row.cells[3].innerText);

      if (currentPrice) {
        const marketValue = currentPrice * units;
        const pl = marketValue - (buyPrice * units);

        row.querySelector('.current-price').innerText = `${currentPrice.toFixed(2)}`;
        row.querySelector('.market-value').innerText = `${marketValue.toFixed(2)}`;
        row.querySelector('.pl').innerText = `${pl.toFixed(2)}`;
        
        // Apply color coding for P/L
        row.querySelector('.pl').className = pl >= 0 ? 'pl positive' : 'pl negative';
      }
    });

    // After updating the table, update the top header summary
    updateSummary();

  } catch (err) {
    console.error('Error fetching prices:', err);
  }
}

function updateSummary() {
  let totalMarketValue = 0;
  let totalInvestment = 0;

  // Holdings table
  const rows = document.querySelectorAll('#holdingsTable tbody tr');
  rows.forEach(row => {
    const marketValue = parseMoney(
      row.querySelector('.market-value').innerText
    );

    const units = Number(row.cells[2].innerText);
    const buyPrice = Number(row.cells[3].innerText);

    totalMarketValue += marketValue;
    totalInvestment += units * buyPrice;
  });

  // Static assets (no stocks)
  ['stat-mfs', 'stat-crypto', 'stat-cash'].forEach(id => {
    const el = document.getElementById(id);
    if (!el) return;

    const value = parseMoney(el.innerText);
    totalMarketValue += value;
    totalInvestment += value;
  });

  const returnPercent =
    totalInvestment > 0
      ? ((totalMarketValue - totalInvestment) / totalInvestment) * 100
      : 0;

  document.getElementById('total-portfolio-value').innerText =
    `₹${totalMarketValue.toFixed(0)}`;

  const returnEl = document.getElementById('total-return-percent');
  returnEl.innerText = `${returnPercent >= 0 ? '+' : ''}${returnPercent.toFixed(2)}%`;
  returnEl.className = returnPercent >= 0 ? 'positive' : 'negative';
}


window.addEventListener('load', updatePrices);
setInterval(updatePrices, REFRESH_INTERVAL);