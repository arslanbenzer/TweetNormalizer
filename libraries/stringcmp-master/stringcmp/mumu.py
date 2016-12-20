def editex(str1, str2, min_threshold = None):
  """Return approximate string comparator measure (between 0.0 and 1.0)
     using the editex distance.

  USAGE:
    score = editex(str1, str2, min_threshold)

  ARGUMENTS:
    str1           The first string
    str2           The second string
    min_threshold  Minimum threshold between 0 and 1

  DESCRIPTION:
    Based on ideas described in:

    "Phonetic String Matching: Lessons Learned from Information Retrieval"
    by Justin Zobel and Philip Dart, SIGIR 1995.

    Important: This function assumes that the input strings only contain
    letters and whitespace, but no other characters. A whitespace is handled
    like a slient sounds.
  """

  # Quick check if the strings are empty or the same - - - - - - - - - - - - -
  #
  if (str1 == '') or (str2 == ''):
    return 0.0
  elif (str1 == str2):
    return 1.0

  n = len(str1)
  m = len(str2)

  # Values for edit costs - - - - - - - - - - - - - - - - - - - - - - - - - - -
  #
  BIG_COSTS = 3  # If characters are not in same group
  SML_COSTS = 2  # If characters are in same group

  # Mappings of letters into groups - - - - - - - - - - - - - - - - - - - - - -
  #
  groupsof_dict = {'a':0, 'b':1, 'c':2, 'd':3, 'e':0, 'f':1, 'g':2, 'h':7,
                   'i':0, 'j':2, 'k':2, 'l':4, 'm':5, 'n':5, 'o':0, 'p':1,
                   'q':2, 'r':6, 's':2, 't':3, 'u':0, 'v':1, 'w':7, 'x':2,
                   'y':0, 'z':2, '{':7}

  # Function to calculate cost of a deletion - - - - - - - - - - - - - - - - -
  #
  def delcost(char1, char2, groupsof_dict):

    if (char1 == char2):
      return 0

    code1 = groupsof_dict.get(char1,-1)  # -1 is not a char
    code2 = groupsof_dict.get(char2,-2)  # -2 if not a char

    if (code1 == code2) or (code2 == 7):  # Same or silent
      return SML_COSTS  # Small difference costs
    else:
      return BIG_COSTS

  # - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  if (' ' in str1):
    str1 = str1.replace(' ','{')
  if (' ' in str2):
    str2 = str2.replace(' ','{')

  if (n > m):  # Make sure n <= m, to use O(min(n,m)) space
    str1, str2 = str2, str1
    n, m =       m, n

  row = [0]*(m+1)  # Generate empty cost matrix
  F = []
  for i in range(n+1):
    F.append(row[:])

  F[1][0] = BIG_COSTS   # Initialise first row and first column of cost matrix
  F[0][1] = BIG_COSTS

  sum = BIG_COSTS
  for i in range(2,n+1):
    sum += delcost(str1[i-2], str1[i-1], groupsof_dict)
    F[i][0] = sum

  sum = BIG_COSTS
  for j in range(2,m+1):
    sum += delcost(str2[j-2], str2[j-1], groupsof_dict)
    F[0][j] = sum

  for i in range(1,n+1):

    if (i == 1):
      inc1 = BIG_COSTS
    else:
      inc1 = delcost(str1[i-2], str1[i-1], groupsof_dict)

    for j in range(1,m+1):
      if (j == 1):
        inc2 = BIG_COSTS
      else:
        inc2 = delcost(str2[j-2], str2[j-1], groupsof_dict)

      if (str1[i-1] == str2[j-1]):
        diag = 0
      else:
        code1 = groupsof_dict.get(str1[i-1],-1)  # -1 is not a char
        code2 = groupsof_dict.get(str2[j-1],-2)  # -2 if not a char

        if (code1 == code2):  # Same phonetic group
          diag = SML_COSTS
        else:
          diag = BIG_COSTS

      F[i][j] = min(F[i-1][j]+inc1, F[i][j-1]+inc2, F[i-1][j-1]+diag)

  w = 1.0 - float(F[n][m]) / float(max(F[0][m],F[n][0]))

  if (w < 0.0):
    w = 0.0

  assert (w >= 0.0) and (w <= 1.0), 'Similarity weight outside 0-1: %f' % (w)

  # A log message - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
  #
  logging.debug('Editex comparator string "%s" with "%s" value: %.3f' \
                % (str1, str2, w))
  return w