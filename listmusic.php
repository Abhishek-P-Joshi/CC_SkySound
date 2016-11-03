<?php
/*Make connection with database*/
$con = mysqli_connect("localhost","cl19-music-db","Pokemon2016!","cl19-music-db");

/*Function to check the connection*/

if(mysqli_connect_errno()){
printf("Connection failed : %s\n",mysqli_connect_error());
exit();
}


/*Scan directory for files*/

$files = glob('*.mp3');

usort ($files, function($a,$b){
return filemtime($a) < filemtime($b);

});


/*Insert list of files to the database if they dont exist*/

$i = 0;

/*While we still have files at location i*/
while($files[$i]){

$TrackName = basename($files[$i]);
echo $TrackName."**";

$addquery = "INSERT INTO songs(Id, TrackName, NumberOfLikes, NumberOfPlays) VALUES(default, '$TrackName' , '0', '0')";
mysqli_query($con, $addquery);


$i++;



} 
?>
