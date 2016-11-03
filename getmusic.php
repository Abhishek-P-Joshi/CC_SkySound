<?php

$con = mysqli_connect("localhost", "cl19-music-db","Pokemon2016!","cl19-music-db" );

$query = "SELECT * FROM songs ORDER BY Id DESC";

if($result = mysqli_query($con,$query)){

    $i = 0;
    while($row = mysqli_fetch_row($result)){
        if($i == 0){
                               echo "$row[0],$row[1],$row[2],$row[3]";
                       }else{
                               echo "*$row[0],$row[1],$row[2],$row[3]";
                       }
                       $i++;

    }
    mysqli_free_result($result);

} 

mysqli_close($con);

?>
