#!/usr/bin/ruby

print <<-EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head profile="http://selenium-ide.openqa.org/profiles/test-case">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="selenium.base" href="http://localhost" />
<title>testcase_issue227</title>
</head>
<body>
<table cellpadding="1" cellspacing="1" border="1">
<thead>
<tr><td rowspan="1" colspan="3">testcase_issue227</td></tr>
</thead><tbody>
<tr>
	<td>open</td>
	<td>/issue227.html</td>
	<td></td>
</tr>
EOF

(1..87).each do |i|
  n = (i % 3 == 1) ? '' : 'Not'
  print <<-EOF
<tr>
	<td>verify#{n}Editable</td>
	<td>id=id_#{i}</td>
	<td></td>
</tr>
  EOF
end

print <<-EOF

</tbody></table>
</body>
</html>
EOF
