//File for uploading from sd card to the server

<?php

   $incomingdata = $_SERVER['QUERY_STRING'];
   $data = substr($incomingdata, strpos($incomingdata, 'id=')+3);
  
    $file_path = "uploads/";
    $file_path .= $data;
    $file_path .= "/";
echo $file_path;

     
    $file_path = $file_path . basename( $_FILES['uploaded_file']['name']);
    if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path) ){
        echo "success";
    } else{
        echo "fail";
    }
 ?>
