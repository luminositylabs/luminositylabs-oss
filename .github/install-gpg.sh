#!/usr/bin/env bash

set -euf

sudo apt-get update
sudo apt-get install -y gnupg haveged

rm -rf ~/.gnupg
gpg --list-keys

cat >key-info <<EOF
    %echo Generating a key
    Key-Type: RSA
    Key-Length: 4096
    Subkey-Type: RSA
    Subkey-Length: 4096
    Name-Real: Luminosity Labs LLC
    Name-Comment: Luminosity Labs Development Team
    Name-Email: test-key@luminositylabs.net
    Expire-Date: 0
    %no-ask-passphrase
    %no-protection
    %commit
    %echo done
EOF

gpg --verbose --batch --gen-key key-info

echo -e "5\ny\n" |  gpg --no-tty --command-fd 0 --expert --edit-key test-key@luminositylabs.net trust;

# test
gpg --list-keys
gpg -e -a -r test-key@luminositylabs.net key-info
rm key-info
gpg -d key-info.asc
rm key-info.asc

set +euf

