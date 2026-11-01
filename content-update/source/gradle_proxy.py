"""Use the inherited cloud proxy and public system CA trust without credentials.
Print only JVM selector arguments, never proxy user info or environment contents.
"""
import os
import re
from pathlib import Path
from urllib.parse import urlsplit

for scheme in ['http','https']:
 raw=os.environ.get(scheme.upper()+'_PROXY') or os.environ.get(scheme+'_proxy')
 if raw:
  p=urlsplit(raw)
  if not p.hostname or not re.fullmatch(r'[a-zA-Z0-9.-]+',p.hostname):raise ValueError('Unsupported configured proxy hostname')
  print(f'-D{scheme}.proxyHost={p.hostname}')
  print(f'-D{scheme}.proxyPort={p.port or 8080}')
trust=Path('/etc/ssl/certs/java/cacerts')
if trust.is_file():print('-Djavax.net.ssl.trustStore='+str(trust))
