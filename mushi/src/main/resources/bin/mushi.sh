#!/bin/sh

path="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
java -cp "$path/../lib/*" org.mule.mushi.MushiMain $@