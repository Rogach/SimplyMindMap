a=$(cat $( find src/ -type f | grep .java ) | wc -l);
echo $a/92822 \($[$a*100/92822]%\)

for d in $(find src/ -type d); do 
  if [ "$(find $d -type f | grep \.java$)" != "" ]; then 
    printf "%5d  %s\n" $(cat $(find $d -type f | grep \.java$) | wc -l) $d; 
  fi 
done
