<?php
require("dbconnect.php");
$result=mysqli_query($conn,"SELECT file_name from notes_details ORDER BY id DESC LIMIT 1");
$row=mysqli_fetch_array($result);
$file_name_from_db=$row['file_name'];
$file_name = $file_name_from_db.".pdf";
$server_path = "documents/";
$web_path = "http://project700007.netai.net/college/documents/";

$file = $server_path.$file_name;
file_put_contents($file,"");

$fp = fopen("php://input", 'r');
while ($buffer =  fread($fp, 8192)) {
    file_put_contents($file,$buffer,FILE_APPEND | LOCK_EX);
}
exit;
?>