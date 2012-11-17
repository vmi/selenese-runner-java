#!/usr/bin/perl

use strict;

my %ide = ();
my %sr = ();

sub load_list {
  my ($hash, $file) = @_;
  open(LIST, $file) or die "Can't open $file ($!)";
  while (<LIST>) {
    chomp;
    $hash->{$_} = 1;
  }
  close(LIST);
}

load_list(\%ide, $ARGV[0]);
load_list(\%sr, $ARGV[1]);

my $out = $ARGV[2];

if ($out eq '-s') {
print "[Supported command]\n";
for my $name (sort keys %sr) {
  if ($sr{$name}) {
    print "$name\n";
  }
}
}

if ($out eq '-u') {
print "[Unsupported command]\n";
for my $name (sort keys %ide) {
  if (!$sr{$name}) {
    print "$name\n";
  }
}
}

if ($out eq '-o') {
print "[selenese-runner only]\n";
for my $name (sort keys %sr) {
  if (!$ide{$name}) {
    print "$name\n";
  }
}
}
