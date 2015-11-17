const int **ipp;
int *ip;
const int i = 42;
 
void func(void) {
  ipp = &ip; /* Constraint violation */
  *ipp = &i; /* Valid */
  *ip = 0;   /* Modifies constant i (was 42) */
}
/*


 void func(unsigned int a, unsigned int b) {
  unsigned int usum = 956 +2147483647 ; //Warning Wrap 

  signed int suma = -6 + 2147483645; //Warning Overflow

  signed int division = 3 / 0; //Error Division por 0 

  signed int division = 3 % 0; //Error Division por 0   

  for (int i= 0; i < 10; i++){

  }
}
*/