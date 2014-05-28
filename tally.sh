a=$(cat $( find src/ -type f | grep .java ) | wc -l); echo $a/92822 \($[$a*100/92822]%\)
