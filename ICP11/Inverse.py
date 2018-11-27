lines = open('Triplets','r').readlines()
for line1 in lines:
    terms1 = line1.split(",")
    sub1 = terms1[0]
    pre1 = terms1[1]
    obj1 = terms1[2]
    for line2 in lines:
        terms2 = line2.split(",")
        sub2 = terms2[0]
        pre2 = terms2[1]
        obj2 = terms2[2]
        print( sub1+" "+obj2)
        if str(sub1)==str(obj2):
            print ("yes")
            if obj1==sub2:
                print (pre1+"--"+pre2)



