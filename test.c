void open_some_file(const char *file) {
  FILE *f = fopen(file, "r");
  if (NULL != f) {
    /* File exists, handle error */
  } else {
    if (fclose(f) == EOF) {
      /* Handle error */
    }
    f = fopen(file, "w");
    if (NULL == f) {
      /* Handle error */
   }
  
    /* Write to file */
  if (fclose(f) == EOF) {
      /* Handle error */
    }
  }
}

id func(void) {
  static volatile int **ipp;
  static int *ip;
  static volatile int i = 0;
 
  printf("i = %d.\n", i);
 
  ipp = &ip; /* May produce a warning diagnostic */
  ipp = (int**) &ip; /* Constraint violation; may produce a warning diagnostic */
  *ipp = &i; /* Valid */
  }


 void func(unsigned int a, unsigned int b) {
  signed int m = a +b;

  signed int usum = 956 +2147483647 ; //Warning Wrap 

  signed int suma = -6 + 2147483645; //Warning Overflow

  signed int division = 3 / 0; //Error Division por 0 

  signed int division = 3 % 0; //Error Division por 0   

  for (int i= 0; i < 10; i++){

  }
}