          Rop readme

1.Check the rop jar signature

 (1)install rop PGP public key to your machine
 $ gpg --keyserver hkp://pool.sks-keyservers.net --recv-keys 56E18958

 (2)run the following command to check the signature
 $gpg --verify rop-1.0.jar.asc

  NOTE:download GnuPGP:
  a)For windows user:
    http://www.vertigrated.com/blog/2011/03/releasing-maven-artifacts-to-central-repository-through-sonatype/
  b)For Linux user:
    http://www.gpgtools.org/

2. GETTING STARTED
   http://rop.group.iteye.com/