  int close_stdout(void) {
    if (fclose(stdout) == EOF) {
      return -1;
    }

    //fputs("stdout successfully closed.", stderr);
    
    printf("stdout successfully closed.\n");
    return 0;
  }