#!/usr/bin/perl

use strict;

my $mode = "";

while (<>) {
  if (m@^<a name="(\w+)"></a>$@) {
    $mode = $1;
  }
  if (m@^<strong><a name="(\w+)"></a>@) {
    print "$1\n";
  } elsif ($mode eq 'accessors') {
    if (m@^<li>(\w+)$@) {
      print "$1\n";
    }
  }
}
