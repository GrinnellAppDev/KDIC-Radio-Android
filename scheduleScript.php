<?php
/**
Created by Prabir Pradhan [pradhanp].
Last edited on October 5, 2015.

Description: A PHP script to parse the schedule for KDIC shows, represented in
             the website as an HTML table, into JSON to be pulled by the Android app.
**/

header ( 'Content-type: text/json; charset=utf-8' );

// URL of HTML page with the schedule (which is an HTML table)
$SCHEDULE_URL = 'http://kdic.grinnell.edu/?page_id=118';


// parse html
$html_data = file_get_contents($SCHEDULE_URL);
$dom = new domDocument;
@$dom->loadHTML($html_data);
$dom->preserveWhiteSpace = false;

// the schedule table is the first (and only) element with tag <table> in the web page
$schedule_table = $dom->getElementsByTagName('table')->item(0);

// all the rows in the schedule table
$rows = $schedule_table->getElementsByTagName('tr');


// get the days (first row of the table) as an array
$days = array();
$first_row_cols = $rows->item(0)->getElementsByTagName('td');

// number of colums in the table
$num_cols = $first_row_cols->length;

for ($i = 1; $i < $num_cols; $i++) { // don't include the first element
	$days[] = $first_row_cols->item($i)->textContent;
}

// get the times (first column of the table) as an array
$times = array();
for ($i = 1; $i < $rows->length; $i++) {
	$times[] = $rows->item($i)->getElementsByTagName('td')->item(0)->textContent;
}

// get the show names as a table (2D array)
$table_data = array();
for ($i = 1; $i < $num_cols; $i++) {
	$show_day = array();

	for ($j = 1; $j < $rows->length; $j++) 
		$show_day[$times[$j - 1]]= $rows->item($j)->getElementsByTagName('td')->item($i)->textContent;

	$table_data[$days[$i - 1]] = $show_day;
}

// organize classes for JSON
$json_data = new stdClass();
$json_data->days = $days;
$json_data->times = $times;
$json_data->data = $table_data;

// display the json encoded data 
echo json_encode($json_data);

?>