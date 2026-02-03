Chart.defaults.color = "#cbd5f5";
Chart.defaults.font.family = "Inter";

new Chart(document.getElementById("allocationChart"),{
  type:"doughnut",
  data:{
    labels:["Stocks","Bonds","Crypto","Cash"],
    datasets:[{
      data:[60,15,10,15],
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
        label:"Portfolio",
        data:[120000,128000,135000,142000,152430],
        borderColor:"#2563eb",
        backgroundColor:"rgba(37,99,235,0.25)",
        fill:true,
        tension:0.4
      },
      {
        label:"S&P 500",
        data:[120000,125000,130000,134000,138000],
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
      y:{ticks:{callback:v=>`$${v/1000}k`}}
    }
  }
});
