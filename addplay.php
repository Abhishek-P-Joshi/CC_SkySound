<?php

$con = mysqli_connect("localhost", "cl19-music-db","Pokemon2016!","cl19-music-db" );

$incomingdata = $_SERVER['QUERY_STRING'];
$data = substr($incomingdata, strpos($incomingdata,'id=')+3);
$id=mysqli_real_escape_string($con, $data);

echo "$id";

$query = "UPDATE songs SET NumberOfPlays = NumberOfPlays + 1 WHERE Id = '$id'";

mysqli_query($con,$query);


?>
