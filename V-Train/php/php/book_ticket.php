<?php
include("dbconnect.php");
$pnr = $_POST["pnr"];
$class = $_POST["class"];
$user_id = $_POST["user_id"];
$toStation = $_POST["toStation"];
$fromStation = $_POST["fromStation"];
$trainName = $_POST["trainName"];
$date = $_POST["date"];
$response= array();
$response["success"] = false;
$result = mysqli_query($conn, "INSERT INTO `booked_ticket`(`class`, `pnr`, `user_id`,`toStation`,`fromStation`,`date`,`trainName`) VALUES ('$class','$pnr','$user_id','$toStation','$fromStation','$date','$trainName')");
$affected=mysqli_affected_rows($conn);
if($affected!=-1)
{
    $response["success"] = true;  
}
echo json_encode($response);
mysqli_close($conn);
exit();
?>