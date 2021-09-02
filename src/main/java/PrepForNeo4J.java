import Utils.CmdUtils;
import Utils.Parameters;
import Utils.ReadLines;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class PrepForNeo4J {

    private String outputDir;

    private int movieInfoVertexId = 0;
    private int movieInfoIdxVertexId = 0;
    private int personInfoVertexId = 0;
    private HashMap<String, Integer> movieInfoMap;
    private HashMap<String, Integer> movieInfoIdxMap;
    private HashMap<String, Integer> personInfoMap;

    public PrepForNeo4J(String outDir) throws IOException {
        outputDir = outDir;

        movieInfoMap = new HashMap<>();
        movieInfoIdxMap = new HashMap<>();
        personInfoMap = new HashMap<>();
    }


    private int getMovieInfoVertexId() {
        int info = movieInfoVertexId;
        movieInfoVertexId++;
        return info;
    }

    private int getMovieInfoIdxVertexId() {
        int info = movieInfoIdxVertexId;
        movieInfoIdxVertexId++;
        return info;
    }

    private int getPersonInfoVertexId() {
        int info = personInfoVertexId;
        personInfoVertexId++;
        return info;
    }


    private String prepString(String s) {
        if (!s.isEmpty() && s.charAt(s.length()-1) == '\\' && s.charAt(s.length()-2) != '\\') {
            s = s + "\\";
        }
        s = s.replace("\\", "\\\\");
        return s.replace("\"", "\"\"");
    }

    public void toNeo4jFormat(String baseLoc, String outDir) throws IOException {
        System.out.print("Working on 'title'...");
        addTitle(baseLoc);
        System.out.print("DONE\nWorking on 'akaTitle'...");
        addAkaTitle(baseLoc);
        System.out.print("DONE\nWorking on 'companies'...");
        addCompanies(baseLoc);
        System.out.print("DONE\nWorking on 'movieComapnyEdges'...");
        addMovieCompanyEdges(baseLoc);
        System.out.print("DONE\nWorking on 'movieInfo'...");
        addMovieInfo(baseLoc);
        System.out.print("DONE\nWorking on 'keyword'...");
        addKeyword(baseLoc);
        System.out.print("DONE\nWorking on 'movieLink'...");
        addMovieLink(baseLoc);
        System.out.print("DONE\nWorking on 'person'...");
        addPerson(baseLoc);
        System.out.print("DONE\nWorking on 'akaName'...");
        addAkaName(baseLoc);
        System.out.print("DONE\nWorking on 'personInfo'...");
        addPersonInfo(baseLoc);
        System.out.print("DONE\nWorking on 'character'...");
        addCharacter(baseLoc);
        System.out.print("DONE\nWorking on 'castInfo'...");
        addCastInfo(baseLoc);
        System.out.print("DONE\nWorking on 'completeCastInfo'...");
        addComplCastInfo(baseLoc);
        System.out.print("DONE");
    }

    private void addTitle(String baseLoc) throws IOException {
        HashMap<Integer, String> kindTypes = new HashMap<>();
        ReadLines.readCsv(baseLoc + "kind_type.csv", (String[] line, int i) -> {
            int id = Integer.parseInt(line[0]);
            kindTypes.put(id, line[1]);
        });

        FileWriter wr = new FileWriter(outputDir + "titles.csv");
        wr.write("movieId:ID,title,imdb_index," +
                "kind,production_year:int,imdb_id:int," +
                "phonetic_code,season_nr:int,episode_nr:int," +
                "series_years,:LABEL\n");

        FileWriter wrEpisodeOfEdges = new FileWriter(outputDir + "episodeOfEdges.csv");
        wrEpisodeOfEdges.write(":START_ID,:END_ID,:TYPE\n");

        ReadLines.readCsv(baseLoc + "title.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
                wrEpisodeOfEdges.flush();
            }
            String movieId = Parameters.titleIdPrepend+line[0];

            // create edge
            String episodeOfId = Parameters.titleIdPrepend+line[7];
            if (!line[7].equals(""))
                wrEpisodeOfEdges.write(movieId + "," + episodeOfId + ",episode_of\n");

            // Create vertex
            int kindId = Integer.parseInt(line[3]);
            wr.write(movieId + ",\"" + prepString(line[1]) + "\",\"" + prepString(line[2]) + "\"," +
                    "\"" + kindTypes.get(kindId) + "\"," + prepString(line[4]) + "," + prepString(line[5]) + "," +
                    "\""+ prepString(line[6]) + "\"," + line[8] + "," + line[9] +"," +
                    "\"" +prepString(line[10]) + "\",title\n");

        });
        wr.flush();
        wr.close();

        wrEpisodeOfEdges.flush();
        wrEpisodeOfEdges.close();
    }

    public void addAkaTitle(String baseLoc) throws IOException {
        FileWriter wAkaTitle = new FileWriter(outputDir + "akaTitle.csv");
        wAkaTitle.write("akaTitleId:ID,title,:LABEL\n");

        FileWriter wAkaTitleEdge = new FileWriter(outputDir + "akaTitleEdges.csv");
        wAkaTitleEdge.write(":START_ID,:END_ID,:TYPE\n");


        ReadLines.readCsv(baseLoc + "aka_title.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wAkaTitle.flush();
                wAkaTitleEdge.flush();
            }
            String akaTitleId = Parameters.akaTitleIdPrepend+line[0];
            String akaTitle = line[2];
            String movieId = Parameters.titleIdPrepend+line[1];

            // Add vertex akaTitle
            wAkaTitle.write(akaTitleId + ",\"" + prepString(akaTitle) + "\",aka_title\n");

            if (!line[1].equals("0")) {
                // Add edge movie to akaTitle
                wAkaTitleEdge.write(movieId + "," + akaTitleId + ",aka_title\n");
            }
        });
        wAkaTitle.flush();
        wAkaTitle.close();

        wAkaTitleEdge.flush();
        wAkaTitleEdge.close();


    }

    public void addCompanies(String baseLoc) throws IOException {
        FileWriter wr = new FileWriter(outputDir + "company_name.csv");
        wr.write("companyId:ID,name,country_code," +
                "imdb_id:int,name_pcode_nf,name_pcode_sf,:LABEL\n");
        ReadLines.readCsv(baseLoc + "company_name.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
            }
            String comapanyId = Parameters.companyIdPrepend+line[0];
            wr.write(comapanyId + ",\"" + prepString(line[1]) + "\",\"" + prepString(line[2]) + "\"," +
                    line[3] + ",\"" + prepString(line[4]) + "\",\"" + prepString(line[5]) + "\",company\n");

        });
        wr.flush();
        wr.close();
    }

    public void addMovieCompanyEdges(String baseLoc) throws IOException {
        HashMap<Integer, String> compTypes = new HashMap<>();
        ReadLines.readCsv(baseLoc + "company_type.csv", (String[] line, int i) -> {
            int id = Integer.parseInt(line[0]);
            compTypes.put(id, line[1]);
        });

        FileWriter wr = new FileWriter(outputDir + "movie_companies.csv");
        wr.write(":START_ID,:END_ID,:TYPE,note\n");
        ReadLines.readCsv(baseLoc + "movie_companies.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
            }
            String movieId = Parameters.titleIdPrepend+line[1];
            String companyId = Parameters.companyIdPrepend+line[2];
            int companyTypeId = Integer.parseInt(line[3]);
            String compType = compTypes.get(companyTypeId);
            wr.write(movieId + "," + companyId + "," + compType + ",\"" + prepString(line[4]) + "\"\n" );
        });
        wr.flush();
        wr.close();
    }

    public void addMovieInfo(String baseLoc) throws IOException {
        HashMap<Integer, String> infoTypes = new HashMap<>();
        ReadLines.readCsv(baseLoc + "info_type.csv", (String[] line, int i) -> {
            int id = Integer.parseInt(line[0]);
            infoTypes.put(id, line[1]);
        });

        FileWriter wr = new FileWriter(outputDir + "infoVertices.csv");
        wr.write("infoId:ID,info,:LABEL\n");

        FileWriter wrEdge = new FileWriter(outputDir + "infoEdges.csv");
        wrEdge.write(":START_ID,:END_ID,:TYPE,note\n");

        ReadLines.readCsv(baseLoc + "movie_info.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
                wrEdge.flush();
            }
            String movieId = Parameters.titleIdPrepend+line[1];
            String info = line[3];

            int infoIdInt;
            String infoId;
            if (movieInfoMap.containsKey(info)) {
                infoIdInt = movieInfoMap.get(info);
                infoId = Parameters.movieInfoPrepend+infoIdInt;
            } else {
                infoIdInt = getMovieInfoVertexId();
                movieInfoMap.put(info, infoIdInt);

                infoId = Parameters.movieInfoPrepend+infoIdInt;
                wr.write(infoId + ",\"" + prepString(info) + "\",movieInfo\n" );
            }

            int infoTypeId = Integer.parseInt(line[2]);
            String infoType = infoTypes.get(infoTypeId);
            wrEdge.write(movieId + "," + infoId + "," + infoType + ",\"" + prepString(line[4]) + "\"\n");
        });
        // Copy past except movie_info_idx
        ReadLines.readCsv(baseLoc + "movie_info_idx.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
                wrEdge.flush();
            }

            String movieId = Parameters.titleIdPrepend+line[1];
            String info = line[3];

            int infoIdInt;
            String infoId;
            if (movieInfoIdxMap.containsKey(info)) {
                infoIdInt = movieInfoIdxMap.get(info);
                infoId = Parameters.movieInfoIdxPrepend+infoIdInt;
            } else {
                infoIdInt = getMovieInfoIdxVertexId();
                movieInfoIdxMap.put(info, infoIdInt);

                infoId = Parameters.movieInfoIdxPrepend+infoIdInt;
                wr.write(infoId + ",\"" + prepString(info) + "\",movieInfoIdx\n" );
            }

            int infoTypeId = Integer.parseInt(line[2]);
            String infoType = infoTypes.get(infoTypeId);
            wrEdge.write(movieId + "," + infoId + "," + infoType + ",\"" + prepString(line[4]) + "\"\n");
        });

        wr.flush();
        wr.close();

        wrEdge.flush();
        wrEdge.close();
    }

    public void addPersonInfo(String baseLoc) throws IOException {
        HashMap<Integer, String> infoTypes = new HashMap<>();
        ReadLines.readCsv(baseLoc + "info_type.csv", (String[] line, int i) -> {
            int id = Integer.parseInt(line[0]);
            infoTypes.put(id, line[1]);
        });

        FileWriter wr = new FileWriter(outputDir + "personInfoVertices.csv");
        wr.write("infoId:ID,info,:LABEL\n");

        FileWriter wrEdge = new FileWriter(outputDir + "personInfoEdges.csv");
        wrEdge.write(":START_ID,:END_ID,:TYPE,note\n");

        ReadLines.readCsv(baseLoc + "person_info.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
                wrEdge.flush();
            }

            String personId = Parameters.personIdPrepend+line[1];
            String info = line[3];

            int infoIdInt;
            String infoId;
            if (personInfoMap.containsKey(info)) {
                infoIdInt = personInfoMap.get(info);
                infoId = Parameters.personInfoPrepend+infoIdInt;
            } else {
                infoIdInt = getPersonInfoVertexId();
                personInfoMap.put(info, infoIdInt);

                if (infoIdInt == 95059) {
                    int brkpt = 1;
                }
                infoId = Parameters.personInfoPrepend+infoIdInt;
                String toWrite = infoId + ",\"" + prepString(info) + "\",personInfo\n";
                wr.write(toWrite);
            }

            int infoTypeId = Integer.parseInt(line[2]);
            String infoType = infoTypes.get(infoTypeId);
            wrEdge.write(personId + "," + infoId + "," + infoType + ",\"" + prepString(line[4]) + "\"\n");
        });

        wr.flush();
        wr.close();

        wrEdge.flush();
        wrEdge.close();
    }


    public void addKeyword(String baseLoc) throws IOException {
        FileWriter wr = new FileWriter(outputDir + "keyword.csv");
        wr.write("keywordId:ID,keyword,phonetic_code,:LABEL\n");

        ReadLines.readCsv(baseLoc + "keyword.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
            }
            String keywordId = Parameters.keywordPrepend + line[0];
            wr.write(keywordId + ",\"" + prepString(line[1]) + "\",\"" + prepString(line[2]) + "\",keyword\n");
        });
        wr.flush();
        wr.close();

        FileWriter wrEdges = new FileWriter(outputDir + "keywordEdges.csv");
        wrEdges.write(":START_ID,:END_ID,:TYPE\n");

        ReadLines.readCsv(baseLoc + "movie_keyword.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wrEdges.flush();
            }
            String movieId = Parameters.titleIdPrepend+line[1];
            String keywordId = Parameters.keywordPrepend + line[2];
            wrEdges.write(movieId + "," + keywordId + ",has_keyword\n");
        });
        wrEdges.flush();
        wrEdges.close();
    }


    private void addMovieLink(String baseLoc) throws IOException {
        HashMap<Integer, String> linkTypes = new HashMap<>();
        ReadLines.readCsv(baseLoc + "link_type.csv", (String[] line, int i) -> {
            int id = Integer.parseInt(line[0]);
            linkTypes.put(id, line[1]);
        });

        FileWriter wr = new FileWriter(outputDir + "linkTypeEdges.csv");
        wr.write(":START_ID,:END_ID,:TYPE,type\n");
        ReadLines.readCsv(baseLoc + "movie_link.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
            }
            String movieId = Parameters.titleIdPrepend+line[1];
            String linkedMovieId = Parameters.titleIdPrepend+line[2];
            int linkTypeId = Integer.parseInt(line[3]);
            String linkType = linkTypes.get(linkTypeId);

            wr.write(movieId + "," + linkedMovieId + ",movie_link," + linkType + "\n");
        });


        wr.flush();
        wr.close();
    }

    private void addPerson(String baseLoc) throws IOException {
        FileWriter wr = new FileWriter(outputDir + "person.csv");
        wr.write("personId:ID,name,imdb_index,imdb_id:int,gender,name_pcode_cf,name_pcode_nf,surname_pcode,:LABEL\n");
        ReadLines.readCsv(baseLoc + "name.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
            }
            String personId = Parameters.personIdPrepend+line[0];

            wr.write(personId + ",\"" + prepString(line[1]) + "\",\"" + prepString(line[2]) +
                    "\"," + line[3] + ",\"" + prepString(line[4]) + "\",\"" + prepString(line[5]) +
                    "\",\"" + prepString(line[6]) + "\",\"" + prepString(line[7]) + "\",person\n");
        });

        wr.flush();
        wr.close();
    }

    private void addAkaName(String baseLoc) throws IOException {
        FileWriter wr = new FileWriter(outputDir + "akaName.csv");
        wr.write("akaNameId:ID,name,:LABEL\n");

        FileWriter wrEdges = new FileWriter(outputDir + "akaNameEdges.csv");
        wrEdges.write(":START_ID,:END_ID,:TYPE\n");

        ReadLines.readCsv(baseLoc + "aka_name.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
                wrEdges.flush();
            }

            String akaNameId = Parameters.akaNameIdPrepend+line[0];

            wr.write(akaNameId + ",\"" + prepString(line[2]) + "\",aka_name\n");

            String personId = Parameters.personIdPrepend+line[1];

            wrEdges.write(personId + "," + akaNameId + ",aka_name\n");
        });

        wr.flush();
        wr.close();

        wrEdges.flush();
        wrEdges.close();
    }


    private void addCharacter(String baseLoc) throws IOException {
        FileWriter wr = new FileWriter(outputDir + "character.csv");
        wr.write("characterId:ID,name,imdb_index,imdb_id:int,name_pcode_nf,surname_pcode,:LABEL\n");

        ReadLines.readCsv(baseLoc + "char_name.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
            }

            String charId = Parameters.characterIdPrepend+line[0];

            wr.write(charId + ",\"" + prepString(line[1]) + "\",\"" + prepString(line[2]) + "\"," +
                    line[3] + ",\"" + prepString(line[4]) + "\",\"" + prepString(line[5]) + "\",character\n");
        });

        wr.flush();
        wr.close();
    }

    private void addCastInfo(String baseLoc) throws IOException {
        HashMap<Integer, String> roleTypes = new HashMap<>();
        ReadLines.readCsv(baseLoc + "role_type.csv", (String[] line, int i) -> {
            int id = Integer.parseInt(line[0]);
            roleTypes.put(id, line[1]);
        });

        FileWriter wrCI = new FileWriter(outputDir + "castInfoVertices.csv");
        wrCI.write("castInfoId:ID,note,nr_order:int,role,:LABEL\n");

        FileWriter wr = new FileWriter(outputDir + "castInfoEdges.csv");
        wr.write(":START_ID,:END_ID,:TYPE\n");

        ReadLines.readCsv(baseLoc + "cast_info.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
                wrCI.flush();
            }

            String castInfoId = Parameters.castInfoIdPrepend+line[0];
            String note = line[4];
            String nr_order = line[5];
            int roleTypeId = Integer.parseInt(line[6]);
            String roleType = roleTypes.get(roleTypeId);

            wrCI.write(castInfoId + ",\"" + prepString(note) + "\"," + nr_order + ",\"" + prepString(roleType)+"\",cast_info\n");

            String personId = Parameters.personIdPrepend+line[1];
            String movieId = Parameters.titleIdPrepend+line[2];


            wr.write(castInfoId + "," + personId + ",cast_info_person\n");
            wr.write(castInfoId + "," + movieId + ",cast_info_movie\n");
            if (!line[3].equals("")) {
                String characterId = Parameters.characterIdPrepend+line[3];
                wr.write(castInfoId + "," + characterId + ",cast_info_character\n");
            }
        });

        wrCI.flush();
        wrCI.close();

        wr.flush();
        wr.close();
    }

    private void addComplCastInfo(String baseLoc) throws IOException {
        HashMap<Integer, String> compCastTypes = new HashMap<>();
        ReadLines.readCsv(baseLoc + "comp_cast_type.csv", (String[] line, int i) -> {
            int id = Integer.parseInt(line[0]);
            compCastTypes.put(id, line[1]);
        });

        FileWriter wrCI = new FileWriter(outputDir + "complCastInfoVertices.csv");
        wrCI.write("complCastInfoId:ID,subject,status,:LABEL\n");

        FileWriter wr = new FileWriter(outputDir + "complCastInfoEdges.csv");
        wr.write(":START_ID,:END_ID,:TYPE\n");

        ReadLines.readCsv(baseLoc + "complete_cast.csv", (String[] line, int i) -> {
            if (i % 100001 == 0) {
                wr.flush();
                wrCI.flush();
            }

            String complCastId = Parameters.completeCastPrepend+line[0];
            String movieId = Parameters.titleIdPrepend+line[1];
            int subjectId = Integer.parseInt(line[2]);
            String subject = compCastTypes.get(subjectId);

            int statusId = Integer.parseInt(line[3]);
            String status = compCastTypes.get(statusId);

            wrCI.write(complCastId + ",\"" + prepString(subject) + "\",\"" + prepString(status) + "\",complete_cast\n");

            wr.write(complCastId + "," + movieId + ",complete_cast_movie\n");
        });

        wrCI.flush();
        wrCI.close();

        wr.flush();
        wr.close();
    }


    public static void main(String[] args) throws Exception {
        if (CmdUtils.hasOption(args, "-dataDir")) {
//            String dataDir = "D:\\JOB_data\\";
            String dataDir = CmdUtils.getOption(args, "-dataDir");

            String outDir = "";
            if (CmdUtils.hasOption(args, "-outDir")) {
                outDir = CmdUtils.getOption(args, "-outDir");
            }
            PrepForNeo4J prepForNeo4J = new PrepForNeo4J(outDir);
            prepForNeo4J.toNeo4jFormat(dataDir, outDir);
        } else {
            System.out.println("Specify the directory containing the uncompressed JOB data files");
        }
    }
}
