//PHP Script to Create Subdirectories

<?php

/*Make connection with database*/
$con = mysqli_connect("localhost","cl19-music-db","Pokemon2016!","cl19-music-db");

/*Function to check the connection*/

if(mysqli_connect_errno()){
printf("Connection failed : %s\n",mysqli_connect_error());
exit();
}



$incomingdata = $_SERVER['QUERY_STRING'];
$data = substr($incomingdata, strpos($incomingdata,'id=')+3);

echo "$data";

    if (!is_dir($data)) {
    mkdir($data);

    echo "not there";
}

else {
    echo "there";
}


$query = "SELECT * FROM $data";

$result= mysqli_query($con,$query);
if(empty($result)){
   echo "Table Not Found";
   $query = "CREATE TABLE $data ( Id int(11) AUTO_INCREMENT PRIMARY KEY, SongTitle varchar(255) NOT NULL)";
$result= mysqli_query($con,$query);
}


?>
