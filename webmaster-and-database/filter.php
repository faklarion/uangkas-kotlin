<?php
    require_once('connection.php');

    $from = $_REQUEST['from'];
    $to = $_REQUEST['to'];
    $sql = "SELECT * FROM transaksi WHERE (tanggal >= '$from') AND (tanggal <= '$to') ORDER BY transaksi_id DESC";
    //echo $sql; die();
    
    $query = mysqli_query($conn, $sql );

    $result = array();
    while($row = mysqli_fetch_array($query)){
        array_push($result,array(
            'transaksi_id'  => $row['transaksi_id'],
            'status'        => $row['status'],
            'jumlah'        => $row['jumlah'],
            'keterangan'    => $row['keterangan'],
            'tanggal'       => date("d/m/Y", strtotime($row['tanggal'])),
            'tanggal2'      => $row['tanggal']
        ));
 }

    $sql =
    "SELECT
    (SELECT SUM(jumlah) FROM transaksi WHERE status='MASUK' AND (tanggal >= '$from') AND (tanggal <= '$to') ) AS masuk,
    (SELECT SUM(jumlah) FROM transaksi WHERE status='KELUAR' AND (tanggal >= '$from') AND (tanggal <= '$to')) AS keluar";
    //echo $sql;die();
    $query = mysqli_query($conn, $sql );

    while ($row = mysqli_fetch_assoc($query)) {

        $masuk = $row['masuk'];
        $keluar = $row['keluar'];

    }

    echo json_encode(array(
        'masuk' => $masuk,
        'keluar' => $keluar,
        'saldo' => ($masuk - $keluar),
        'result' => $result
        ));
        
    mysqli_close($conn);
?>