#! /bin/bash
openssl s_client -showcerts -connect 192.168.0.179:5000 </dev/null 2>/dev/null|openssl x509 -outform PEM > my_service_certifcate.pem
