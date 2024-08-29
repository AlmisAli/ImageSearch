% Almis Ali 300317688
% dataset(DirectoryName)
% this is where the image dataset is located
dataset(DirectoryName) :-
    absolute_file_name('./imageDataset2_15_20/', DirectoryName).

% directory_textfiles(DirectoryName, ListOfTextfiles)
% produces the list of text files in a directory
directory_textfiles(D,Textfiles):- directory_files(D,Files), include(isTextFile, Files, Textfiles).
isTextFile(Filename):-string_concat(_,'.txt',Filename).

% read_hist_file(Filename,ListOfNumbers)
% reads a histogram file and produces a list of numbers (bin values)
read_hist_file(Filename,Numbers):- open(Filename,read,Stream),read_line_to_string(Stream,_),
                                   read_line_to_string(Stream,String), close(Stream),
								   atomic_list_concat(List, ' ', String),atoms_numbers(List,Numbers).

% similarity_search(QueryFile,SimilarImageList)
% returns the list of images similar to the query image
% similar images are specified as (ImageName, SimilarityScore)
% predicat dataset/1 provides the location of the image set
similarity_search(QueryFile,SimilarList) :- dataset(D), directory_textfiles(D,TxtFiles),
                                            similarity_search(QueryFile,D,TxtFiles,SimilarList), !.

% similarity_search(QueryFile, DatasetDirectory, HistoFileList, SimilarImageList)
similarity_search(QueryFile, DatasetDirectory, DatasetFiles, Best) :-
    absolute_file_name('./queryImages/', QueryDirectory),
    atom_concat(QueryDirectory, QueryFile, FullQueryPath),
    read_hist_file(FullQueryPath, QueryHisto),
    compare_histograms(QueryHisto, DatasetDirectory, DatasetFiles, Scores),
    sort(2, @>, Scores, Sorted),
    take(Sorted, 5, Best).

% compare_histograms(QueryHisto,DatasetDirectory,DatasetFiles,Scores)
% compares a query histogram with a list of histogram files
compare_histograms(QueryHisto, DatasetDirectory, DatasetFiles, Scores) :- findall([File, S], (member(File, DatasetFiles),atom_concat(DatasetDirectory, File, FileDirectory), read_hist_file(FileDirectory, FileHisto), histogram_intersection(QueryHisto, FileHisto, S)), Scores).

% histogram_intersection(Histogram1, Histogram2, Score)
% compute the intersection similarity score between two histograms
% Score is between 0.0 and 1.0 (1.0 for identical histograms)
histogram_intersection(H1,H2,S) :- normalize(H1, NormalizedQ), normalize(H2, NormalizedD), min_sum(NormalizedQ, NormalizedD, S).

% min_sum/3 calculates the sum of the smallest elements between two lists.
% Calculate the sum of the smallest elements between the two lists.
min_sum([],[],0).
min_sum([HeadQ|H1], [HeadD|H2], S) :- min_sum(H1, H2, R), S is min(HeadQ, HeadD) + R.

% normalise(Histogram, HitogramNormalised)
% Calculate the normalized histogram of a normal histogram using the number of pixels.

normalize(Histo, Normalized) :- pixelAmount(Pixels), bagof(N1, Number^(member(Number, Histo), N1 is Number / Pixels), Normalized).

pixelAmount(172800).

% take(List,K,KList)
% extracts the K first items in a list
take(Src,N,L) :- findall(E, (nth1(I,Src,E), I =< N), L).

% atoms_numbers(ListOfAtoms,ListOfNumbers)
% converts a list of atoms into a list of numbers
atoms_numbers([],[]).
atoms_numbers([X|L],[Y|T]):- atom_number(X,Y), atoms_numbers(L,T).
atoms_numbers([X|L],T):- \+atom_number(X,_), atoms_numbers(L,T).
