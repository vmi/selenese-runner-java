#!/usr/bin/perl

use strict;

while (<>) {
  if (m@Selenium\.prototype\.(do|get)(\w+)\s+=\s+@) {
    if ($1 eq 'do') {
      print lcfirst($2), "\n";
    } elsif ($1 eq 'get') {
      print "store$2\n";
    }
  }
}
