#####User Story Title: Ignorestop 

<b>As a</b> truck driver <br />
<b>I want to</b> get a new reststop suggestion if I drive by the recomended one <br />
<b>So that I can</b> continue driving if I want to

######Acceptance Criterion 1:
<b>Given</b> that the truck is moving <br />
<b>When</b> the driving time to the next stop is less than the time left in that driving session<br />
<b>Then</b> suggest a new reststop<br />

######Acceptance Criterion 2:
<b>Given</b> that the truck is moving <br />
<b>When</b> the driving time to the next stop is more than the time left in that driving session<br />
<b>Then</b> alert the driver<br />