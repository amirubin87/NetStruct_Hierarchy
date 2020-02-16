import sys
__author__ = 'Amir Rubin'
'''
This script converts the output fo asd to the format needed by NetStruct_Hierarchy
Note that in the output the delimiters used are commas, with a trailing comma and an empty line in the end.

Parameters are:
input_file output_file
parmas:
    input_file - path to file with genetics data - in each line i all of the individuals alleles at loci i. The format is - space seperating each locus, comma seperating the alleles
    output_file - path to folder in which we write outputs

'''


def main(inputVector):
    if len(inputVector)<2:
        print("Required parameters: input_file output_file.")
        return
    # parse command line options
    input_file = inputVector[1]
    output_file = inputVector[2]

    with open(input_file) as f:
        with open(output_file, 'w') as out_f:
            counter = 0
            for l in f.readlines():
                # skip first line
                if counter > 0:
                    # drop the first element, as it is the individual id
                    values = l.split()[1 + counter:]
                    if len(values) > 0:
                        new_l = ','.join(values) + ',\n'
                        out_f.write(new_l)
                counter += 1


if __name__ == "__main__":
    main(sys.argv)
