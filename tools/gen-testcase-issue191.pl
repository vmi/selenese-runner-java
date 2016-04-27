#!/usr/bin/perl

use strict;

### Templates

my $html_name = "src/test/resources/htdocs/issue191.html";
my $tc_name = "src/test/resources/selenese/testcase_issue191.html";

my $html_header = <<'EOF';
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Issue #191</title>
</head>
<body>
  <h1>Issue #191</h1>
  <hr>
  <table>
EOF

my $html_footer = <<'EOF';
  </table>
</body>
</html>
EOF

my $tc_header = <<'EOF';
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head profile="http://selenium-ide.openqa.org/profiles/test-case">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="selenium.base" href="http://localhost/" />
<title>testcase_issue191</title>
</head>
<body>
<table cellpadding="1" cellspacing="1" border="1">
<thead>
<tr><td rowspan="1" colspan="3">testcase_issue191</td></tr>
</thead><tbody>
<tr>
	<td>open</td>
	<td>/issue191.html</td>
	<td></td>
</tr>
EOF

my $tc_footer = <<'EOF';

</tbody></table>
</body>
</html>
EOF

### Initialize

my @h_wss = qw( 0009 000A 000D 0020 00A0 );

my $h_wss_max = scalar(@h_wss);

### Generator

open(my $hfh, ">", $html_name) or die "Can't open $html_name ($!)";
open(my $tfh, ">", $tc_name) or die "Can't open $tc_name ($!)";
print $hfh $html_header;
print $tfh $tc_header;

my $id_cnt = "A";

sub gen_code {
  my ($html_msg1, $html_msg2, $tc_msg, $with_not) = @_;
  $with_not = $with_not ? "Not" : "";
  print $hfh <<EOF;
    <tr><td>$id_cnt: $html_msg1</td><td id="$id_cnt">$html_msg2</td></tr>
EOF
  print $tfh <<EOF;
<tr>
	<td>verify${with_not}Text</td>
	<td>css=#$id_cnt</td>
	<td>$tc_msg</td>
</tr>
<!-- $html_msg1 -->
EOF
  $id_cnt++;
}

my $t_ws = '0020';
for my $h_ws (@h_wss) {
  gen_code("U+$h_ws-U+$t_ws", "word1&#x$h_ws;word2", "word1&#x$t_ws;word2");
  if ($h_ws ne '00A0') {
    gen_code("U+$h_ws*2-U+$t_ws", "word1&#x$h_ws;&#x$h_ws;word2", "word1&#x$t_ws;word2");
  }
  gen_code("U+$h_ws/U+$h_ws/U+$h_ws-U+$t_ws", "&#x$h_ws;word1&#x$h_ws;word2&#x$h_ws;", "word1&#x$t_ws;word2");
}
gen_code("br-br", "word1<br>word2", "word1<br />word2");
gen_code("br-LF", "word1<br>word2", "word1&#x000A;word2");
gen_code("LF-LF(not)", "word1&#x000A;word2", "word1&#x000A;word2", 1);
gen_code("LF-br(not)", "word1&#x000A;word2", "word1<br />word2", 1);
gen_code("br/br-br(not)", "word1<br><br>word2", "word1<br />word2", 1);
gen_code("br/br-br/br", "word1<br><br>word2", "word1<br /><br />word2");

print $hfh $html_footer;
print $tfh $tc_footer;
close($hfh);
close($tfh);

print <<EOF;
Generated:
* $html_name
* $tc_name
EOF
