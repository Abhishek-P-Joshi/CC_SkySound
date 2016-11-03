//PHP Script to populate the tables

<?php
/*Make connection with database*/
$con = mysqli_connect("localhost","cl19-music-db","Pokemon2016!","cl19-music-db");

/*Function to check the connection*/

if(mysqli_connect_errno()){
printf("Connection failed : %s\n",mysqli_connect_error());
exit();
}


/*Scan directory for files*/

$incomingdata = $_SERVER['QUERY_STRING'];
$data = substr($incomingdata, strpos($incomingdata, 'id=')+3);
$pieces = explode("&id2=",$data);
echo $pieces[0];
echo $pieces[1];

/*Insert list of files to the database if they dont exist*/

$addquery = "INSERT INTO $pieces[0](Id, SongTitle) VALUES(default, '$pieces[1]')";
mysqli_query($con, $addquery);


?>
