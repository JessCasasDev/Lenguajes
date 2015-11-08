id func(void) {
  static volatile int **ipp;
  static int *ip;
  static volatile int i = 0;
 
  printf("i = %d.\n", i);
 
  ipp = &ip; /* May produce a warning diagnostic */
  ipp = (int**) &ip; /* Constraint violation; may produce a warning diagnostic */
  *ipp = &i; /* Valid */
  }


 void func() {
  unsigned int usum = 956 + 2147483645; //Warning Wrap 

  signed int suma = -6 + 2147483645; //Warning Overflow

}