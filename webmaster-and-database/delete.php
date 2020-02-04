<?php
    require_once('connection.php');
    
    $transaksi_id = $_REQUEST['transaksi_id'];

    // delete
    $query = mysqli_query($conn, "DELETE FROM transaksi WHERE                    transaksi_id='$transaksi_id' ");

    if($query) {
        echo json_encode(array( 'response'=>'success' ));
    } else {
        echo json_encode(array( 'response'=>'failed' ));
    }
?>