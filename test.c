 int close_stdout(void) {
    if (fclose(stdout) == EOF) {
      return -1;
    }

    //fputs("stdout successfully closed.", stderr);
    
    printf("stdout successfully closed.\n");
    return 0;
  }

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
  unsigned int a = 956;
  unsigned int b = 2147483647;
  unsigned int usum = a +b ; //Warning Wrap 
  
  signed int c = -128731;
  signed int d = -2147483647;
  signed int suma = c + d; //Warning Overflow

  signed int division = 3 / 0; //Error Division por 0 

  signed int division = 3 % 0; //Error Division por 0   
 
}

  void func(void) {
  signed int si = -56;
  /* Cast eliminates warning */
  unsigned int ui = (unsigned int)si;
 
  /* ... */
}