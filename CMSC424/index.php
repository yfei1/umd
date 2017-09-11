<!DOCTYPE html>
<html>
<title>Presedential Election Database</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="http://www.w3schools.com/lib/w3.css">
<link rel="stylesheet" href="http://www.w3schools.com/lib/w3-theme-teal.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<style>
input[type=radio].css-checkbox {
							position:absolute; z-index:-1000; left:-1000px; overflow: hidden; clip: rect(0 0 0 0); height:1px; width:1px; margin:-1px; padding:0; border:0;
						}

						input[type=radio].css-checkbox + label.css-label {
							padding-left:20px;
							height:15px; 
							display:inline-block;
							line-height:15px;
							background-repeat:no-repeat;
							background-position: 0 0;
							font-size:15px;
							vertical-align:middle;
							cursor:pointer;

						}

						input[type=radio].css-checkbox:checked + label.css-label {
							background-position: 0 -15px;
						}
						label.css-label {
				background-image:url(http://csscheckbox.com/checkboxes/u/csscheckbox_5a1f35c674e05f90728a036a5a666a30.png);
				-webkit-touch-callout: none;
				-webkit-user-select: none;
				-khtml-user-select: none;
				-moz-user-select: none;
				-ms-user-select: none;
				user-select: none;
			}
th {width:100px;}
.switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 34px;
}

/* Hide default HTML checkbox */
.switch input {display:none;}

/* The slider */
.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 26px;
  width: 26px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
}

input:checked + .slider {
  background-color: #2196F3;
}

input:focus + .slider {
  box-shadow: 0 0 1px #2196F3;
}

input:checked + .slider:before {
  -webkit-transform: translateX(26px);
  -ms-transform: translateX(26px);
  transform: translateX(26px);
}

/* Rounded sliders */
.slider.round {
  border-radius: 17px;
}

.slider.round:before {
  border-radius: 50%;
}
.dropdown {
    /* Size and position */
    position: relative; /* Enable absolute positioning for children and pseudo elements */
    width: 200px;
    padding: 10px;
    margin: 0 auto;

    /* Styles */
    background: #fff;
    color: #9bc7de;
    outline: none;
    cursor: pointer;

    /* Font settings */
    font-weight: bold;
}
td {margin: 10px;padding: 5px;text-align:center}
tr:hover {background-color: #f5f5f5}
.w3-sidenav a {padding:16px}
.navimg {float:left;width:33.33% !important}
</style>
<body>
<nav class="w3-sidenav w3-collapse w3-white w3-animate-left w3-large" style="z-index:3;width:300px;" id="mySidenav">
<ul class="w3-navbar w3-black w3-center">
  <li class="navimg">
    <a href="javascript:void(0)" onclick="openNav('nav03')">
    <i class="fa fa-file w3-xlarge"></i></a></li>
</ul>
<div id="nav01">
  <a href="javascript:void(0)" onclick="w3_close()" class="w3-text-teal w3-hide-large w3-closenav w3-large">Close ×</a>
  <a data-scroll href="#q1">Query1: Given Election Year</a>
  <a data-scroll href="#q2">Query2: Given Candidate</a>
  <a data-scroll href="#q3">Query3: Re-elected on non-contiguous times</a>
  <a data-scroll href="#q4">Query4: Swing Candidates</a>
  <a data-scroll href="#q5">Query5: Party Historical Query</a>
  <a data-scroll href="#q6">Query6: Third Party Candidate Info Query</a>
  <a data-scroll href="#q7">Query7: Most Popular Candidate Query</a>
  <a data-scroll href="#q8">Query8: Lost Poll But Won Election Query</a>
</div>
</nav>
<div class="w3-overlay w3-hide-large" onclick="w3_close()" style="cursor:pointer" id="myOverlay"></div>

<div class="w3-main" style="margin-left:300px;"> 

<div id="myTop" class="w3-top w3-container w3-padding-16 w3-theme w3-large w3-hide-large">
  <i class="fa fa-bars w3-opennav w3-xlarge w3-margin-left w3-margin-right" onclick="w3_open()"></i>
</div>
<header class="w3-container w3-theme w3-padding-64 w3-center">
  <h1 class="w3-xxxlarge w3-padding-16">Presedential Election Database</h1>
  <p><pre style="font-size:20px">If thou gaze long into an abyss, the abyss will also gaze into thee.&#9;&#9;&#9;&#9;- Yufan Fei</pre></p>
</header>

<!-- query1-->
<div id="q1" class="w3-container w3-padding-large w3-section w3-light-grey">
  <h1 class="w3-jumbo">Query1</h1>
  <p class="w3-xlarge">Given Election Year</p>

<form action='' method='GET'>
    <div class="w3-btn w3-theme w3-hover-white" height="100px">Year:  <input type='text' name='year' /><br/></div>
           <input class="w3-btn w3-theme w3-hover-white" type='submit' name='submit' />
           <button class="w3-btn w3-theme w3-hover-white" onclick="myFunction()">Reload page</button>
           <select class="dropdown" name="limit1">
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="30">30</option>
              <option value="40">40</option>
           </select>
           <!-- Rounded switch -->
           <div></div>
           <span style="margin-bottom:50px">Include Poll Result:
             Only affect election after 1936, which is when the first poll began</span>
             <label style="margin:10px; padding:10px" class="switch">
             <input style="margin:1px;" type="checkbox" name='poll' value="checkbox">  
             <span class="slider round"></span>
           </label>
  <p class="w3-large">
  <p><div style="height:300px; overflow:auto;" class="w3-code cssHigh notranslate" >
    <?php echo query1() ?>
  </div>
</div>
<!-- query2-->
<div id="q2" class="w3-container w3-padding-large w3-section w3-light-grey">
  <h1 class="w3-jumbo">Query2</h1>
  <p class="w3-xlarge">Given Candidate</p>

<form action='' method='GET'>
    <div class="w3-btn w3-theme w3-hover-white" height="100px">Candidate Name:  <input type='text' name='name2' /><br/></div>
           <input class="w3-btn w3-theme w3-hover-white" type='submit' name='submit2' />
           <button class="w3-btn w3-theme w3-hover-white" onclick="myFunction()">Reload page</button>
           <div></div>
           <span style="margin-bottom:50px">President</span>
             <label style="margin:10px; padding:10px" class="switch">
             <input style="margin:1px;" type="checkbox" name='pres' value="checkbox">  
             <span class="slider round"></span>
             <span style="margin-bottom:50px; margin-left:60px">Candidate</span>
           </label>
</form>
 
  <p class="w3-large">
  <p><div style="overflow: auto; height:300px;" class="w3-code cssHigh notranslate" >
    <?php echo query2() ?>
  </div>
</div>
<!-- query3-->
<div id="q3" class="w3-container w3-padding-large w3-section w3-light-grey">
  <h1 class="w3-jumbo">Query3</h1>
  <p class="w3-xlarge">Re-elected on non-contiguous times</p>

<form action='' method='GET'>
    <div class="w3-btn w3-theme w3-hover-white" height="100px">Partial Name:  <input type='text' name='name3' /><br/></div>
           <input class="w3-btn w3-theme w3-hover-white" type='submit' name='submit3' />
           <button class="w3-btn w3-theme w3-hover-white" onclick="myFunction()">Reload page</button>
  <p class="w3-large">
  <p><div style="height:300px; overflow:auto;" class="w3-code cssHigh notranslate" >
    <?php echo query3() ?>
  </div>
</div>
<!-- query4-->
<div id="q4" class="w3-container w3-padding-large w3-section w3-light-grey">
  <h1 class="w3-jumbo">Query4</h1>
  <p class="w3-xlarge">Swing Candidates</p>

<form action='' method='GET'>
      <input class="w3-btn w3-theme w3-hover-white" type='submit' name='submit4' />
      <button class="w3-btn w3-theme w3-hover-white" onclick="myFunction()">Reload page</button>
  <p class="w3-large">
  <p><div style="height:300px; overflow:auto;" class="w3-code cssHigh notranslate" >
    <?php echo query4() ?>
  </div>
</div>
<!-- query5-->
<div id="q5" class="w3-container w3-padding-large w3-section w3-light-grey">
  <FIELDSET style=" position: absolute;
    top: 2930px;
    right: 50px;
    font-size: 18px; width:570px; height400px">
      <LEGEND><b>Party Key</b></LEGEND>
      [D] = Democrat; [D-LR] = Democrat-Liberal Republican </br>
      [D-P] = Democrat-Populist; [D-R] = Democrat-Republican</br>
      [F] = Federalist; [N-R] = National-Republican</br>
      [P] = Progressive; [R] = Republican; [W] = Whig</br>
  </FIELDSET>
  <h1 class="w3-jumbo">Query5</h1>
    <th><p class="w3-xlarge">Party Historical Query</p></th>
<form action='' method='GET'>
    <div class="w3-btn w3-theme w3-hover-white" height="100px">Party:  <input type='text' name='name5' /><br/></div>
           <input class="w3-btn w3-theme w3-hover-white" type='submit' name='submit5' />
           <button class="w3-btn w3-theme w3-hover-white" onclick="myFunction()">Reload page</button>
  <p class="w3-large">
  <p><div style="height:300px; overflow:auto;" class="w3-code cssHigh notranslate" >
    <?php echo query5() ?>
  </div>
</div>
<!-- query6-->
<div id="q6" class="w3-container w3-padding-large w3-section w3-light-grey">
  <h1 class="w3-jumbo">Query6</h1>
  <p class="w3-xlarge">Third Party Candidate Info Query</p>

<form action='' method='GET'>
           <input class="w3-btn w3-theme w3-hover-white" type='submit' name='submit6' />
           <button class="w3-btn w3-theme w3-hover-white" onclick="myFunction()">Reload page</button>
  <p class="w3-large">
  <p><div style="height:300px; overflow:auto;" class="w3-code cssHigh notranslate" >
    <?php echo query6() ?>
  </div>
</div>
<!-- query7-->
<div id="q7" class="w3-container w3-padding-large w3-section w3-light-grey">
  <h1 class="w3-jumbo">Query7</h1>
  <p class="w3-xlarge">Most Popular President Query</p>
  <p class="w3-xlarge">NOTE:Most Popular here indicates President who has the largest Popular Vote</p>
<form action='' method='GET'>
    <div class="w3-btn w3-theme w3-hover-white" height="100px">Output Limit:  <input type='text' name='name7' /><br/></div>
           <input class="w3-btn w3-theme w3-hover-white" type='submit' name='submit7' />
           <button class="w3-btn w3-theme w3-hover-white" onclick="myFunction()">Reload page</button>
  <p class="w3-large">
  <p><div style="height:300px; overflow:auto;" class="w3-code cssHigh notranslate" >
    <?php echo query7() ?>
  </div>
</div>
<!-- query8-->
<div id="q8" class="w3-container w3-padding-large w3-section w3-light-grey">
  <h1 class="w3-jumbo">Query8</h1>
  <p class="w3-xlarge">Lost Poll But Won Election Query</p>

<form action='' method='GET'>
           <input class="w3-btn w3-theme w3-hover-white" type='submit' name='submit8' />
           <button class="w3-btn w3-theme w3-hover-white" onclick="myFunction()">Reload page</button>
  <p class="w3-large">
  <p><div style="height:300px; overflow:auto;" class="w3-code cssHigh notranslate" >
    <?php echo query8() ?>
  </div>
</div>

<script>
function w3_open() {
    document.getElementById("mySidenav").style.display = "block";
    document.getElementById("myOverlay").style.display = "block";
}
function w3_close() {
    document.getElementById("mySidenav").style.display = "none";
    document.getElementById("myOverlay").style.display = "none";
}
    document.getElementById(id).style.display = "block";

</script>
<script>
function myFunction() {
    location.reload();
}
</script>

</form>
<script src="dist/js/smooth-scroll.js"></script>
<script>
    smoothScroll.init({
    selector: '[data-scroll]', // Selector for links (must be a class, ID, data attribute, or element tag)
    selectorHeader: null, // Selector for fixed headers (must be a valid CSS selector) [optional]
    speed: 500, // Integer. How fast to complete the scroll in milliseconds
    easing: 'easeInOutCubic', // Easing pattern to use
    offset: 1000, // Integer. How far to offset the scrolling anchor location in pixels
    callback: function ( anchor, toggle ) {} // Function to run after scrolling
});
</script>

</body>

</html>

<?php
  function query8() {
    $mysqli = new mysqli("localhost", "root", "", "ped");
    if ($mysqli->connect_errno) {
      printf("Connect failed: %s\n", $mysqli->connect_error);
      exit();
    }
    if(isset($_GET['submit8'])) {
      if($result = $mysqli->query(
        "SELECT p1.Election, e1.President, p1.pollrate, p2.cand, p2.pollrate
        FROM poll p2, election e1 join poll p1 USING (election) 
        WHERE e1.President = p1.cand and 
        p2.Election = e1.Election and 
        p2.cand != e1.President and 
        p2.pollrate >= p1.pollrate"
      )) {
        printf("Select returned %d rows.<br/>", $result->num_rows);
        printf("<table><tr><th>Election</th><th>President</th><th>President Poll Rate</th><th>Opponent</th><th>Opponent Poll Rate</th></tr>");
        while ($row = $result->fetch_row()) {
            printf ("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", 
            $row[0],$row[1],$row[2],$row[3],$row[4]);
        }
        printf("</table>");
        $result->close();
      }
    }
  }
  function query7() {
    $mysqli = new mysqli("localhost", "root", "", "ped");
    if ($mysqli->connect_errno) {
      printf("Connect failed: %s\n", $mysqli->connect_error);
      exit();
    }
    if(isset($_GET['submit7'])) {
      $temp = $_GET['name7'];
      if($result = $mysqli->query(
        "SELECT election, President, `Popular Vote -Winner`, WinnerParty FROM `election` ORDER BY `Popular Vote -Winner` DESC LIMIT ".$temp
      )) {
        printf("Select returned %d rows.<br/>", $result->num_rows);
        printf("<table><tr><th>Election</th><th>President</th><th>Popular Vote -Winner</th><th>Party Affiliation</th></tr>");
        while ($row = $result->fetch_row()) {
            printf ("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", 
            $row[0],$row[1],$row[2],$row[3]);
        }
        printf("</table>");
        $result->close();
      }
    }
  }
  function query6() {
    $mysqli = new mysqli("localhost", "root", "", "ped");
    if ($mysqli->connect_errno) {
      printf("Connect failed: %s\n", $mysqli->connect_error);
      exit();
    }
    if(isset($_GET['submit6'])) {
      if($result = $mysqli->query(
        "(SELECT election, President as name, WinnerParty as party, TRUE FROM `election` e1
        WHERE e1.WinnerParty != 'D' and e1.WinnerParty != 'R'
        )UNION(
        SELECT election, President as name, OpponentParty as party, FALSE FROM `election` e1
        WHERE e1.OpponentParty != 'D' and e1.OpponentParty != 'R'
        )"
      )) {
        printf("Select returned %d rows.<br/>", $result->num_rows);
        printf("<table><tr><th>Election</th><th>Name</th><th>Party</th><th>W/L</th></tr>");
        while ($row = $result->fetch_row()) {
            printf ("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", 
            $row[0],$row[1],$row[2],$row[3]);
        }
        printf("</table>");
        $result->close();
      }
    }
  }
  function query5() {
    $mysqli = new mysqli("localhost", "root", "", "ped");
    if ($mysqli->connect_errno) {
      printf("Connect failed: %s\n", $mysqli->connect_error);
      exit();
    }
    if(isset($_GET['submit5'])) {
      $temp = $_GET['name5'];
      if ($temp == "") $where = "";
      else $where = "WHERE WinnerParty = '".$temp."'";

      if($result = $mysqli->query(
        "SELECT WinnerParty as Party, SUM(`Electoral Vote -Winner`) as EV, SUM(`Popular Vote -Winner`) as PV, count(WinnerParty)
        FROM `election`".$where.
        "Group by WinnerParty
        ORDER BY SUM(`Electoral Vote -Winner`) DESC"
      )) {
        printf("Select returned %d rows.<br/>", $result->num_rows);
        printf("<table><tr><th>Party</th><th>Electoral Vote</th><th>Party Vote</th><th>Win Counts</th></tr>");
        while ($row = $result->fetch_row()) {
            printf ("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", 
            $row[0],$row[1],$row[2],$row[3]);
        }
        printf("</table>");
        $result->close();
      }
    }
  }
  function query4() {
    $mysqli = new mysqli("localhost", "root", "", "ped");
    if ($mysqli->connect_errno) {
      printf("Connect failed: %s\n", $mysqli->connect_error);
      exit();
    }
    if(isset($_GET['submit4'])) {
      $result1 = $mysqli->query(
        "SELECT e1.Election, e2.Election, e1.President, e1.WinnerParty, e2.WinnerParty  
        FROM election e1, election e2 
        WHERE e1.President = e2.President and e1.WinnerParty != e2.WinnerParty and e1.Election > e2.Election");
      $result2 = $mysqli->query(
        "SELECT e1.Election, e2.Election, e1.President, e1.WinnerParty, e2.OpponentParty
        FROM election e1, election e2 
        WHERE (e1.President = e2.`Main Opponent`and e1.WinnerParty != e2.OpponentParty and e1.Election > e2.Election)");
      $result3 = $mysqli->query(
        "SELECT e1.Election, e2.Election, e1.`Main Opponent`, e1.OpponentParty, e2.OpponentParty
        FROM election e1, election e2 
        WHERE (e1.`Main Opponent` = e2.`Main Opponent`and e1.OpponentParty != e2.OpponentParty and e1.Election > e2.Election)"
      );
      if ($result1||$result2||$result3) {
        $sum = 0;
        if ($result1) $sum = $sum+$result1->num_rows;
        if ($result2) $sum = $sum+$result2->num_rows;
        if ($result3) $sum = $sum+$result3->num_rows;

        printf("Select returned %d rows.<br/>", $sum);
        printf("<table><tr><th>Election1</th><th>Election2</th>
        <th>Name</th><th>Party1</th><th>Party2</th><th>W/L1</th><th>W/L2</th></tr>");
        if ($result1) {
          while ($row = $result1->fetch_row()) {
              printf ("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>W</td><td>W</td></tr>", 
              $row[0],$row[1],$row[2],$row[3],$row[4]);
          }
        }
        if ($result2) {
          while ($row = $result2->fetch_row()) {
              printf ("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>W</td><td>L</td></tr>", 
              $row[0],$row[1],$row[2],$row[3],$row[4]);
          }
        }
        if ($result3) {
          while ($row = $result3->fetch_row()) {
              printf ("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>L</td><td>L</td></tr>", 
              $row[0],$row[1],$row[2],$row[3],$row[4]);
          }
        }
        printf("</table>");
        $result1->close();
      }
    }
  }
  function query3() {
    $mysqli = new mysqli("localhost", "root", "", "ped");
    if ($mysqli->connect_errno) {
      printf("Connect failed: %s\n", $mysqli->connect_error);
      exit();
    }
    if(isset($_GET['submit3'])) {
      $temp = $_GET['name3'];
      if($result = $mysqli->query(
        "SELECT DISTINCT e1.President, e1.Election, e2.Election
        FROM election e1, election e2 
        WHERE e1.President = e2.President and 
        (e1.Election - e2.Election > 4) and
        e1.President like '%".$temp."%' and e1.President != 'Franklin Roosevelt'"
      )) {
        if ($result->num_rows != 0) {
          printf("Select returned 1 rows.<br/>");
          printf("<table><tr><th>President Name</th><th>First Term</th><th>Second Term</th></tr>");
          if ($row = $result->fetch_row()) {
              printf ("<tr><td>%s</td><td>%s</td><td>%s</td></tr>", $row[0], $row[1], $row[2]);
          }
          printf("</table>");
          $result->close();
        } else {
          printf("Requested Result Not Found");
        }
      }
    }
  }
  function query2() {
    $mysqli = new mysqli("localhost", "root", "", "ped");
    if ($mysqli->connect_errno) {
      printf("Connect failed: %s\n", $mysqli->connect_error);
      exit();
    }
    if(isset($_GET['submit2'])) {
      $temp = $_GET['name2'];
      if(!isset($_GET['pres'])) {
        $result = $mysqli->query(
        "SELECT `Election`,`President`,`Main Opponent`,`Vice President`,`WinnerParty`,`OpponentParty` FROM `election` WHERE `president` like '%" .$temp."%'"
        );
        if($result) {
          printf("Select returned %d rows. Current Perspective: PRESIDENT<br/>", $result->num_rows);
          printf("<table><tr><th>Election Year</th><th>President</th><th>Main Opponent</th><th>Vice President</th>
            <th>Winner Party</th><th>Opponent Party</th></tr>");
          while ($row = $result->fetch_row()) {
            printf ("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
            $row[0], $row[1],$row[2],$row[3],$row[4],$row[5]);          
          }
          printf("</table>");
          $result->close();
        }
      } else {
          $result = $mysqli->query(
          "SELECT * FROM `candpool` WHERE `CandidateList` like '%" .$temp."%'"
          );
          if($result) {
            printf("Select returned %d rows. Current Perspective: CANDIDATE<br/>", $result->num_rows);
            printf("<table><tr><th>CandidateList</th><th>Election</th><th>CandidateVote</th></tr>");
            while ($row = $result->fetch_row()) {
                printf ("<tr><td>%s</td><td>%s</td><td>%s</td></tr>", $row[1], $row[0], $row[2]);
            }
            printf("</table>");
            $result->close();
          }
       }
     
    }
  }
  function query1() {
    $mysqli = new mysqli("localhost", "root", "", "ped");
    if ($mysqli->connect_errno) {
      printf("Connect failed: %s\n", $mysqli->connect_error);
      exit();
    }
    if(isset($_GET['submit'])) {
      $temp = $_GET['year'];
      $limit = $_GET['limit1'];
      if($temp == "") {     
          if(isset($_GET['poll'])) {
            if ($result = $mysqli->query(
              "SELECT `Election`,`President`,`Main Opponent`,`Vice President`,`WinnerParty`,`OpponentParty`, p1.pollrate, p2.pollrate
              FROM `election` e1 join poll p1 using (election) join poll p2 using (election) 
              WHERE e1.president = p1.cand and e1.`Main Opponent` = p2.cand
              LIMIT ".$limit)
            ) {
                printf("Select returned %d rows. Option Poll: Checked<br/>", $result->num_rows);
                printf("<table><tr><th>Election Year</th><th>President</th><th>Main Opponent</th><th>Vice President</th>
                <th>Winner Party</th><th>Opponent Party</th><th>President Poll Rate</th><th>Opponent Poll Rate</th></tr>");
                while ($row = $result->fetch_row()) {
                    printf ("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", 
                    $row[0], $row[1],$row[2],$row[3],$row[4],$row[5],$row[6],$row[7]);
                }
              }
          } else {
            if ($result = $mysqli->query(
              "SELECT `Election`,`President`,`Main Opponent`,`Vice President`,`WinnerParty`,`OpponentParty`
              FROM `election` e1 
              LIMIT ".$limit)
            ) {
              printf("Select returned %d rows. Option Poll: Unchecked<br/>", $result->num_rows);
              printf("<table><tr><th>Election Year</th><th>President</th><th>Main Opponent</th><th>Vice President</th>
              <th>Winner Party</th><th>Opponent Party</th></tr>");
              while ($row = $result->fetch_row()) {
                  printf ("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                  $row[0], $row[1],$row[2],$row[3],$row[4],$row[5]);
              }
            }
          }
          printf("</table>");
          $result->close();
      }
      elseif(!preg_match("/[[:digit:]]{4}/",$temp))   {
        printf("Invalid Input: Please use an empty string or a 4-digit number instead!");
        exit();
      }
      #when submit and year are both set
      else{
          if(isset($_GET['poll'])) {
            if ($result = $mysqli->query(
              "SELECT `Election`,`President`,`Main Opponent`,`Vice President`,`WinnerParty`,`OpponentParty`, p1.pollrate, p2.pollrate
              FROM `election` e1 join poll p1 using (election) join poll p2 using (election) 
              WHERE e1.president = p1.cand and e1.`Main Opponent` = p2.cand and Election = " .$temp 
              ." LIMIT ".$limit)
            ) {
                if ($result->num_rows != 0) {
                  printf("Select returned %d rows. Option Poll: Checked<br/>", $result->num_rows);
                  printf("<table><tr><th>Election Year</th><th>President</th><th>Main Opponent</th><th>Vice President</th>
                  <th>Winner Party</th><th>Opponent Party</th><th>President Poll Rate</th><th>Opponent Poll Rate</th></tr>");
                  while ($row = $result->fetch_row()) {
                      printf ("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", 
                      $row[0], $row[1],$row[2],$row[3],$row[4],$row[5],$row[6],$row[7]);
                  }
                } else {printf("No match was found");}
              }
          } else {
            if ($result = $mysqli->query(
              "SELECT `Election`,`President`,`Main Opponent`,`Vice President`,`WinnerParty`,`OpponentParty`
              FROM `election` e1
              WHERE Election = " .$temp." LIMIT ".$limit)
            ) {
              if ($result->num_rows != 0) {
              printf("Select returned %d rows. Option Poll: Unchecked<br/>", $result->num_rows);
              printf("<table><tr><th>Election Year</th><th>President</th><th>Main Opponent</th><th>Vice President</th>
              <th>Winner Party</th><th>Opponent Party</th></tr>");
              while ($row = $result->fetch_row()) {
                  printf ("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                  $row[0], $row[1],$row[2],$row[3],$row[4],$row[5]);
              }
              } else {printf("No match was found");}
            }
          }
          printf("</table>");        
      }
    }
  }
?>