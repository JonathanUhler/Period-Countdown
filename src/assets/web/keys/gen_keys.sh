#!/bin/bash


running_dir=$( dirname -- "$0"; )
transport_keystore="$running_dir/transport_keystore.jks"
transport_certfile="$running_dir/transport_cert.pem"

transport_host="localhost"
validity=365
password="changeit"

usage() {
    echo "Usage: $0 [options]"
    echo "       [-a <host>]     (Set the alias parameter for the transport. Default ${transport_host})"
    echo "       [-u <org unit>] (Set the organizational unit parameter)"
    echo "       [-o <org name>] (Set the organizational name parameter)"
    echo "       [-l <locality>] (Set the locality/city name parameter)"
    echo "       [-s <state>]    (Set the state name parameter)"
    echo "       [-c <country>]  (Set the country code parameter)"
    echo "       [-d <duration>] (Set the valid duration of the certficate. Default $validity)"
    echo "       [-p <password>] (Set the password for key and cert files. Default $password)"
    echo ""
    echo "       [-h]            (Print this message and exit)"
}

while getopts "ha:u:o:l:s:c:d:p:" flag
do
    case $flag in
	h)
	    usage
	    exit
	    ;;
	a)
	    transport_host=${OPTARG}
	    ;;
	u)
	    org_unit=${OPTARG}
	    ;;
	o)
	    org_name=${OPTARG}
	    ;;
	l)
	    locality=${OPTARG}
	    ;;
	s)
	    state=${OPTARG}
	    ;;
	c)
	    country=${OPTARG}
	    ;;
	d)
	    validity=${OPTARG}
	    ;;
	p)
	    password=${OPTARG}
	    ;;
    esac
done


transport_dname="cn=$transport_host, ou=$org_unit, o=$org_name, l=$locality, s=$state, c=$country"

# Generate transport's key pair to use in the certificate (cert contains the public key)
echo "[gen_keys] Creating keystore for $transport_host"
keytool \
    -genkey \
    -alias "$transport_host" \
    -keyalg rsa \
    -dname "$transport_dname" \
    -validity $validity \
    -keystore "$transport_keystore" \
    -storepass "$password"

# Generate transport certificate
echo ""
echo "[gen_keys] Creating certificate for $transport_host"
keytool \
    -export \
    -alias "$transport_host" \
    -file "$transport_certfile" \
    -keystore "$transport_keystore" \
    -rfc \
    -storepass "$password"
