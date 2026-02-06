Chart.defaults.color = "#cbd5f5";
Chart.defaults.font.family = "Inter";

new Chart(document.getElementById("allocationChart"),{
  type:"doughnut",
  data:{
    labels:["Stocks","Bonds","Crypto","Cash"],
    datasets:[{
      data:[60,20,10,10],
      backgroundColor:["#2563eb","#22c55e","#eab308","#94a3b8"],
      borderWidth:0
    }]
  },
  options:{
    cutout:"70%",
    rotation:-90,
    animation:{animateRotate:true,duration:1600},
    plugins:{legend:{position:"bottom"}}
  }
});

new Chart(document.getElementById("performanceChart"),{
  type:"line",
  data:{
    labels:["Apr","May","Jun","Jul","Aug"],
    datasets:[
      {
        label:"Portfolio Value (₹)",
        data:[900000,980000,1050000,1150000,1252430],
        borderColor:"#2563eb",
        backgroundColor:"rgba(37,99,235,0.25)",
        fill:true,
        tension:0.4
      },
      {
        label:"NIFTY 50",
        data:[900000,940000,990000,1030000,1080000],
        borderColor:"#94a3b8",
        borderDash:[6,6],
        fill:false,
        tension:0.4
      }
    ]
  },
  options:{
    plugins:{legend:{position:"bottom"}},
    scales:{
      y:{ticks:{callback:v=>`₹${(v/1000).toFixed(0)}k`}}
    }
  }
});
