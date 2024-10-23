# Random-Configuration-Builder

The Random Configuration Builder can be used to generate synthetic sales using a CNF and installation rates.

# Example result
More information about the input data can be found in the section below [Example input](#example-input).
> c Used Seed: -3952349368590913869  
> c Execution time: 0 seconds  
> c Number of variables: 6  
> c Generated models: 5  
> c Used CNF file: input_data/cnf/Phone_example.cnf  
> c Used Installation rates file: input_data/installation_rates/freq_result_Phone_example_100Decimal.txt  
> c Used procedure: allRandom  
> 010101  
> 001101  
> 001101  
> 001101  
> 010101  

First three sales as tabular:

| Sale number | 64 Gb Storage | 128 Storage | 256 GB Storage | Black color | 8 GB RAM | 16 GB RAM |
|-------------|---------------|-------------|----------------|-------------|----------|-----------|
| Sale 1      | 0             | 1           | 0              | 1           | 0        | 1         |
| Sale 2      | 0             | 0           | 1              | 1           | 0        | 1         |
| Sale 3      | 0             | 0           | 1              | 1           | 0        | 1         |
| ...         | ...           | ...         | ...            | ...         | ...      | ...       |

Some more complex data can be found in the input_data and output_data folders.

# Example Input

As input, you have to provide a CNF and associated Installation Rates.  
More information and input files can ben found in [CNF-Generator](https://github.com/SteffenHub/CNF-Generator) and [Installation-Rate-Builder](https://github.com/SteffenHub/Installation-Rate-Builder)

Installation rates refer to the frequency or percentage with which specific configurable options of a product are selected by users.  
These rates provide insight into user preferences and can help in optimizing product offerings, inventory management, and marketing strategies.

For instance, consider a configurable product like a smartphone that comes with various options such as different storage capacities,  
colors, and RAM capacities. Installation rates would describe how often each of these options is chosen by users. For example:
- 1: 64 GB storage: 40%
- 2: 128 GB storage: 35%
- 3: 256 GB storage: 25%
- 4: Black color: 100%
- 5: 8GB RAM: 60%
- 6: 16GB RAM: 40%

These percentages indicate the popularity of each configuration option among users.

Behind these installation rates, there is a Conjunctive Normal Form (CNF) representation.
The individual choice options, such as storage capacities, form a family, which are in an XOR relationship.
For example a Family of 1, 2 and 3 means you can choose exactly one of them.

In the conjunctive normal form, this is written as:
> ((1 ∨ 2 ∨ 3) ∧ (!1 ∨ !2) ∧ (!1 ∨ !3) ∧ (!2 ∨ !3))

For the Phone example the CNF can look like (src/input_data/cnf/Phone_example.cnf):
> p cnf 6 8  
> c Family for storage  
> 1 2 3 0  
> -1 -2 0  
> -1 -3 0  
> -2 -3 0  
> c Family for color  
> 4 0  
> c Family for accessories  
> 5 6 0  
> -5 -6 0  
> c 64GB Storage is not available with 16 GB RAM(1 -> !6)  
> -1 -6 0

Syntax hints:  
p cnf number_of_variables number_of_rules  
A line beginning with c introduces a comment

In this simple example the installation rates contain at least the rules:
- The sum of installation rates of a family is 100%
- The installation rate of 5(8GB RAM) is greater or equals 1(64 GB Storage)

Associated installation rates generated with the [Installation-Rate-Builder](https://github.com/SteffenHub/Installation-Rate-Builder) (src/input_data/installation_rates/freq_result_Phone_example_100Decimal.cnf): 

> c used cnf: ../data/Phone_example.cnf  
> c 100% vars: 1  
> c 0% vars: 0  
> c used decimal places: 100  
> c used seed: 8467335337672211825  
> c needed time: 0.7834186553955078 seconds  
> 0.11  
> 0.54  
> 0.35  
> 1.0  
> 0.21  
> 0.79

This means for example 11% of all sales are the 64 Gb storage version or 21% of all sales are the 8 GB Ram version.

With this two input files we can generate synthetic sales. See [Example Result](#example-result)


# Run
To run this Code you need to download the Sat4J SAT-solver and integrate it into your Project.  
After setting up the solver you have to run the Main file and answer some questions in a Dialog. See [How to use](#how-to-use)

TODO Some procedures need a nnf from the c2d solver

#### [Download Sat4J](#download-sat4j-1)
*  In IntelliJ go to File -> Project Structure -> Libraries -> + -> Java and select the org.sat4j-core.jar


# Download Sat4J

Releases are available at [OW2 Releases](https://gitlab.ow2.org/sat4j/sat4j/-/releases)

Select [Precompiled binaries](https://release.ow2.org/sat4j/) to download a .jar directly. We used the core-version for this Project.

# How to use

With run the main file we get into a Dialog where we have to answer some questions for the input data.

> Which CNF file should be used. Pass the path like: input_data/cnf/Phone_example.cnf  
> > input_data/cnf/Phone_example.cnf  
> 
> [1, 2, 3]  
> [-1, -2]  
> [-1, -3]  
> [-2, -3]  
> [4]  
> [5, 6]  
> [-5, -6]  
> [-1, -6]  
> 
> Which Installation rate file should be used. Pass the path like: input_data/installation_rates/freq_result_Phone_example_100Decimal.txt  
> > input_data/installation_rates/freq_result_Phone_example_100Decimal.txt
> 
> 0.11 - 0.54 - 0.35 - 1.0 - 0.21 - 0.79 -  
> 
> Which procedure you want to use. Type in a number. Choose between:  
> 1. randomConflictsWithNNF  
> 2. allRandom  
> 3. allRandomExclude01Rates  
> 4. checkConsistency
> 5. treeDownToUp  
> 6. ApproachInstallationRates  
> > 2
> 
> How many models should be build. Example: 100000  
> > 5
> 
> Which seed should be used for the random generator. Example: 8902374. Type 'None' if a random seed should be generated.  
> > None
> 
> -3952349368590913869 set as seed