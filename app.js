const API_URL = "http://127.0.0.1:8000/prices"; // Backend URL
const REFRESH_INTERVAL = 15000; // 15 seconds refresh interval

// Function to fetch and update the current price for multiple symbols
async function updatePrices() {
  // Get all symbols from the table
  const symbols = [];
  const rows = document.querySelectorAll('#holdingsTable tbody tr');
  rows.forEach(row => {
    const symbol = row.getAttribute('data-symbol');
    symbols.push(symbol);
  });

  try {
    // Fetch prices for all symbols
    const res = await fetch(API_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(symbols),
    });

    const data = await res.json();

    // Update table rows with prices
    rows.forEach(row => {
      const symbol = row.getAttribute('data-symbol');
      const currentPrice = data[symbol]?.price;
      const units = parseFloat(row.querySelector('td:nth-child(3)').innerText);
      const buyPrice = parseFloat(row.querySelector('td:nth-child(4)').innerText);

      if (currentPrice) {
        const marketValue = currentPrice * units;
        const pl = marketValue - (buyPrice * units);

        row.querySelector('.current-price').innerText = `₹${currentPrice}`;
        row.querySelector('.market-value').innerText = `₹${marketValue.toFixed(2)}`;
        row.querySelector('.pl').innerText = `₹${pl.toFixed(2)}`;
      } else {
        row.querySelector('.current-price').innerText = 'Error';
      }
    });
  } catch (err) {
    console.error('Error fetching prices:', err);
  }
}

// Fetch prices on page load
window.addEventListener('load', updatePrices);

// Auto refresh every REFRESH_INTERVAL
setInterval(updatePrices, REFRESH_INTERVAL);
