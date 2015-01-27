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
      my @arg_list = split(/,/, $args);
      print STDERR "  Arguments: [", join("] [", @arg_list), "]";
      if ($mode eq 'accessors') {
	# last argument is variable name.
	pop(@arg_list);
	print STDERR " - (remove last argument)";
      }
      print STDERR "\n";
      my $arg_cnt = scalar(@arg_list);
      my @loc_ndx = ();
      for (my $i = 0; $i <= $#arg_list; $i++) {
	push(@loc_ndx, $i) if ($arg_list[$i] =~ /locator/i);
      }
      my $loc_ndx_str = @loc_ndx == 0 ? "" : ", ".join(", ", @loc_ndx);
      print STDERR "  $cmd: $arg_cnt$loc_ndx_str\n";
      print qq'        m.put("$cmd", $arg_cnt$loc_ndx_str);\n';
      $cmd = undef;
      $arg_mode = 0;
    } else {
      # arguments.
      $args .= $_;
    }
  }
}
