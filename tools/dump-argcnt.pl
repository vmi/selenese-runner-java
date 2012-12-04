#!/usr/bin/perl

use strict;

$| = 1;

my $mode = "";
my $cmd = "";
my $arg_mode = 0;
my $args = "";

while (<>) {
  next if (/^\s*$/); # skip line of white space only.
  if (m@^<a name="(\w+)"></a>$@) {
    $mode = $1;
  }
  if (m@^<strong><a name="(\w+)"></a>@) {
    $cmd = $1;
    print STDERR "[$cmd]";
    if ($cmd =~ /^assert/) {
      # skip assert* because unsupported.
      $cmd = undef;
      print STDERR " - skipped.\n";
      next;
    }
    if ($mode eq 'accessors') {
      if ($cmd eq 'store') {
	$cmd = undef;
	print STDERR " - skipped.\n";
	next;
      }
      if ($cmd =~ /Present$|^store(Checked|Editable|Ordered|SomethingSelected|Visible)$/) {
	$cmd =~ s/^store/is/;
      } else {
	$cmd =~ s/^store/get/;
      }
      print STDERR "-> [$cmd]";
    }
    print STDERR "\n";
  }
  if ($cmd && /^\s*\(\s*$/) {
    # start arguments.
    $arg_mode = 1;
    $args = "";
  } elsif ($arg_mode) {
    if (/^\s*\)\s*$/) {
      # end arguments.
      $args =~ s/\s+//gs;
      my $arg_cnt = scalar(split(/,/, $args));
      if ($mode eq 'accessors') {
	# last argument is variable name.
	--$arg_cnt;
      }
      print STDERR "$cmd: $arg_cnt\n";
      print qq'        argCntMap.put("$cmd", $arg_cnt);\n';
      $cmd = undef;
      $arg_mode = 0;
    } else {
      # arguments.
      $args .= $_;
    }
  }
}
