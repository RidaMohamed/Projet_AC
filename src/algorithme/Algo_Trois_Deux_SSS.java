package algorithme;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class Algo_Trois_Deux_SSS {

    static final int COULEUR_ROUGE = 0;
    static final int COULEUR_VERT = 1;
    static final int COULEUR_BLANC = 2;

    private boolean [][][][] binaires ;
    private boolean [][] unaires ;

    private int nb_clauses = 0;
    private int nb_clauses_unaires;
    private int nb_clauses_binaires;
    private int bb = 0;

    private int nb_var;
    private ArrayList<Integer> deuxCouelurs;
    private ArrayList<int[]> listesDesContraintesAsupprimer ;
    private int tabTaille = 0;

    public Algo_Trois_Deux_SSS() throws IOException {
        String defaultDirectory = "res";
        JFileChooser e = new JFileChooser(defaultDirectory);

        int returnVal = e.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            String file = e.getSelectedFile().getPath();
            ajouterContraintes(file);

        }
    }

    /**
     * cette fonction ajoute les contriantes a partir
     * du graphe donne
     * @param e
     * @throws IOException
     */
    public void ajouterContraintes(String e) throws IOException {
        Path f = Paths.get(e);
        InputStream file = new FileInputStream(f.toString());
        InputStreamReader lecture = new InputStreamReader(file);
        BufferedReader buff = new BufferedReader(lecture);
        String line;

        line = buff.readLine();
        String[] info = line.split(":");
        //
        initTablesContarinte(Integer.valueOf(info[1]));

        for(int i= 0; i<Integer.valueOf(info[1]) ;i++){
            line = buff.readLine();
            for (int k = 0 ; k < i ; k++)
                System.out.print(" ");
            for(int j= i ; j < Integer.valueOf(info[1]);j++){

                if (String.valueOf(line.charAt(j)).equals("1")){
                    if (j == i){
                        this.unaires[j][0] = true;
                        this.unaires[j][1] = true;
                        this.unaires[j][2] = true;
                        this.nb_clauses = this.nb_clauses + 3 ;
                        System.out.print(line.charAt(j));
                    }else {
                        //*********** regler l'ajout des contraintes
                        this.binaires[j][i][0][0] = true ;
                        this.nb_clauses++ ;
                        this.binaires[j][i][1][1] = true ;
                        this.nb_clauses++ ;
                        this.binaires[j][i][2][2] = true ;
                        this.nb_clauses++ ;

                        System.out.print(line.charAt(j));
                    }
                }else
                    System.out.print(line.charAt(j));

            }

            System.out.println("");
        }
        System.out.println("-----------------------");

        supprimerConraintes();
    }

    /**
     * cette fonction initisalise les 2 tableaux
     * des contraintes unaires et binaires
     * @param n
     */
    public void initTablesContarinte(int n){

        this.unaires = new boolean[n][3];
        this.binaires = new boolean[n][n][3][3];


        for (int i = 0 ; i < n ; i++){
            for (int j = 0 ; j < n ; j++){
                for (int k = 0 ; k < 3 ; k ++){
                    for (int u = 0 ; u <3 ; u++) {
                        this.binaires[i][j][k][u] = false ;
                    }
                }
            }
        }

        for (int i = 0 ; i < n ; i++){
            unaires[i][0] = false;
            unaires[i][1] = false;
            unaires[i][2] = false;
        }
    }

    /**
     *
     * @param varX
     */
    public void cas_2(int varX , int c1 , int c2, int co){

        //supprission de var X de toutes les contrainbtes unaires
        this.unaires[varX][c1] = false ;
        this.unaires[varX][c2] = false ;
        this.nb_clauses = this.nb_clauses - 2;

        //supprission de var X de toutes les contrainbtes binares
            for (int j = 0 ; j < this.binaires.length ; j++){
                if(j != varX) {
                    for (int k = 0; k < 3; k++) {
                        for (int u = 0; u < 3; u++) {
                            //les arretes sortantes
                            if (this.binaires[varX][j][k][u]) {
                                this.binaires[varX][j][k][u] = false;
                                this.nb_clauses -- ;
                                if (k == co){
                                    this.unaires[j][u] = true ;
                                    this.nb_clauses++;
                                }
                            }

                            // supprimons l'autre cote des couleurs les arretes entrantes
                            if (this.binaires[j][varX][k][u]) {
                                this.binaires[j][varX][k][u] = false;
                                this.nb_clauses -- ;
                                if (u == co){
                                    this.unaires[j][k] = true ;
                                    this.nb_clauses++;
                                }
                            }

                        }
                    }
                }
            }

    }

    /**
     * cette fonction represente le cas 3
     * d'algo
     * @param varX
     */
    public void cas_3(int varX , int c){

        this.unaires[varX][c] = false ;
        this.nb_clauses--;


        for (int j = 0 ; j < this.binaires.length ; j ++){
                for (int u = 0 ; u < 3 ; u++){
                    if (this.binaires[varX][j][c][u]){
                        this.binaires[varX][j][c][u] = false;
                        this.nb_clauses-- ;
                    }

                    if (this.binaires[j][varX][u][c]){
                        this.binaires[j][varX][u][c] = false;
                        this.nb_clauses-- ;
                    }
                }
        }

        //recueprer les 2 autres coueleurs
        recupererDeuxAutresCouleurs(c);

        listesDesContraintesAsupprimer =
                new ArrayList<>();

        for (int j = 0 ; j < this.binaires.length; j ++){
                for (int u = 0 ; u < 3 ; u++){
                    if (this.binaires[varX][j][this.deuxCouelurs.get(0)][u]){
                        int[] e = new int[4];
                        e[0] = varX;
                        e[1] = j ;
                        e[2] = this.deuxCouelurs.get(0);
                        e[3] = u;
                        listesDesContraintesAsupprimer.add(e);
                        ///////////////////////////////2eme couleur
                        for (int i = 0 ; i< this.binaires.length ; i ++){
                                for (int k = 0 ; k < 3 ; k++){
                                        if (this.binaires[varX][i][this.deuxCouelurs.get(1)][k]){
                                            int[] e2 = new int[4];
                                            e2[0] = varX;
                                            e2[1] = i ;
                                            e2[2] = this.deuxCouelurs.get(1);
                                            e2[3] = k;
                                            listesDesContraintesAsupprimer.add(e2);
                                            if (j == i ){
                                                if (u == k)
                                                    this.unaires[j][u] = true;//ajouter une contrainte de type[(z,c)]
                                                    //avec y == z et b == c
                                            }
                                            else {
                                                //***** ajoute de la contrainte
                                                this.binaires[j][i][u][k] = true;
                                                this.nb_clauses++;
                                            }
                                        }////////////////////
                                        if (this.binaires[i][varX][k][this.deuxCouelurs.get(1)]){
                                            int[] e2 = new int[4];
                                            e2[0] = i;
                                            e2[1] = varX;
                                            e2[2] = k;
                                            e2[3] = this.deuxCouelurs.get(1);
                                            listesDesContraintesAsupprimer.add(e2);
                                            //***** ajoute de la contrainte
                                            if (j == i ){
                                                if (u == k)
                                                    this.unaires[j][u] = true;//ajouter une contrainte de type[(z,c)]
                                                //avec y == z et b == c
                                            }
                                            else {
                                                //***** ajoute de la contrainte
                                                this.binaires[j][i][u][k] = true;
                                                this.nb_clauses++;
                                            }
                                        }
                                }
                        }
                        //////////////////////////////////
                    }

                    if (this.binaires[j][varX][u][this.deuxCouelurs.get(0)]){
                        int[] e = new int[4];
                        e[0] = j;
                        e[1] = varX;
                        e[2] = u;
                        e[3] = this.deuxCouelurs.get(0);
                        listesDesContraintesAsupprimer.add(e);
                        ///////////////////////////////2eme couleur
                        for (int i = 0 ; i< this.binaires.length ; i ++){
                                for (int k = 0 ; k < 3 ; k++){
                                        if (this.binaires[varX][i][this.deuxCouelurs.get(1)][k]){
                                            int[] e2 = new int[4];
                                            e2[0] = varX;
                                            e2[1] = i ;
                                            e2[2] = this.deuxCouelurs.get(1);
                                            e2[3] = k;
                                            listesDesContraintesAsupprimer.add(e2);
                                            //***** ajoute de la contrainte
                                            if (j == i ){
                                                if (u == k)
                                                    this.unaires[j][u] = true;//ajouter une contrainte de type[(z,c)]
                                                //avec y == z et b == c
                                            }
                                            else {
                                                //***** ajoute de la contrainte
                                                this.binaires[j][i][u][k] = true;
                                                this.nb_clauses++;
                                            }
                                        }////////////////////////////
                                        if (this.binaires[i][varX][k][this.deuxCouelurs.get(1)]){
                                            int[] e2 = new int[4];
                                            e2[0] = i;
                                            e2[1] = varX;
                                            e2[2] = k;
                                            e2[3] = this.deuxCouelurs.get(1);
                                            listesDesContraintesAsupprimer.add(e2);
                                            //*****
                                            if (j == i ){
                                                if (u == k)
                                                    this.unaires[j][u] = true;//ajouter une contrainte de type[(z,c)]
                                                //avec y == z et b == c
                                            }
                                            else {
                                                //***** ajoute de la contrainte
                                                this.binaires[j][i][u][k] = true;
                                                this.nb_clauses++;
                                            }
                                        }
                                }
                        }
                        //////////////////////////////////
                    }
                }

        }

        int[] er ;
        for (int i = 0 ; i < this.listesDesContraintesAsupprimer.size() ; i++){
            er = this.listesDesContraintesAsupprimer.get(i);
            if (this.binaires[er[0]][er[1]][er[2]][er[3]] == true){
                this.binaires[er[0]][er[1]][er[2]][er[3]] = false;
                this.nb_clauses--;
            }

        }

        //supprimerCeuxAsupprimer();
    }

    /**
     * Cette fonction represente le cas 4
     * de l'algo
     */
    public void cas_4(int varX , int varY , int c1 , int c2){

        Random r = new Random();
        int valeur = 0 + r.nextInt(4);

        switch (valeur) {
            case 0 :
                this.unaires[varX][c1] = true;
                //
                if (c2 == COULEUR_ROUGE)
                    this.unaires[varY][COULEUR_VERT] = true;
                else if(c2 == COULEUR_VERT)
                    this.unaires[varY][COULEUR_ROUGE] = true;
                else
                    this.unaires[varY][COULEUR_ROUGE] = true;
                //
                this.nb_clauses= this.nb_clauses + 2;
                break;
            case 1 :
                this.unaires[varX][c1] = true;
                //
                if (c2 == COULEUR_ROUGE)
                    this.unaires[varY][COULEUR_BLANC] = true;
                else if(c2 == COULEUR_VERT)
                    this.unaires[varY][COULEUR_BLANC] = true;
                else
                    this.unaires[varY][COULEUR_VERT] = true;
                //
                this.nb_clauses= this.nb_clauses + 2;
                break;
            case 2 :
                if (c1 == COULEUR_ROUGE)
                    this.unaires[varX][COULEUR_VERT] = true;
                else if(c1 == COULEUR_VERT)
                    this.unaires[varX][COULEUR_ROUGE] = true;
                else
                    this.unaires[varX][COULEUR_ROUGE] = true;
                //
                //
                this.unaires[varY][c2] = true;
                this.nb_clauses= this.nb_clauses + 2;
                break;
            case 3 :
                if (c1 == COULEUR_ROUGE)
                    this.unaires[varX][COULEUR_BLANC] = true;
                else if(c1 == COULEUR_VERT)
                    this.unaires[varX][COULEUR_BLANC] = true;
                else
                    this.unaires[varX][COULEUR_VERT] = true;
                //
                this.unaires[varY][c2] = true;
                this.nb_clauses= this.nb_clauses + 2;
                break;
        }


    }

    /**
     * cette fonction verfier s'il reste encore
     * des ciobtraintes a traiter
     * @return false ou true
     */
    public boolean verfierContraiteRestantes(){

        if(this.nb_clauses == 0)
            return false;

        return true;// signifie que il restes des contraintes plus de vars
    }

    /**
     * cette fonction ajoute les deux couleurs
     * pour le cas 3
     * @param c
     */
    public void recupererDeuxAutresCouleurs(int c){
        deuxCouelurs = new ArrayList<Integer>();
        if (c == COULEUR_ROUGE ){
            deuxCouelurs.add(COULEUR_VERT);
            deuxCouelurs.add(COULEUR_BLANC);
        }
        else if(c == COULEUR_VERT){
            deuxCouelurs.add(COULEUR_ROUGE);
            deuxCouelurs.add(COULEUR_BLANC);
        }
        else {
            deuxCouelurs.add(COULEUR_ROUGE);
            deuxCouelurs.add(COULEUR_VERT);
        }
    }

    /**
     * cette fonciton supprime
     * les contraintes de cas 3
     */
    public void supprimerCeuxAsupprimer(){
     //   System.out.println(this.nb_clauses);
        for (int i = 0 ; i < this.listesDesContraintesAsupprimer.size() ; i++){
            int[] e = this.listesDesContraintesAsupprimer.get(i);
            System.out.println(e[0]+" / "+e[1]+" / "+e[2]+" / "+e[3]);
            System.out.println("----- " + this.binaires[e[0]][e[1]][e[2]][e[3]]);
            this.binaires[e[0]][e[1]][e[2]][e[3]] = false;
            this.nb_clauses--;
        }
        //System.exit(0);
     //   System.out.println(this.nb_clauses);
    }

    /**
     * Execution de l'algo
     * @return
     */
    public void supprimerConraintes(){

        boolean b = true;// signifie que il restes des contraintes il reste des vars

        OUTER_LOOP :
        while (b) {

            //cas 1 : il y a une variable x qui apparaît dans 3 contraintes unaires
            for(int i = 0 ; i < this.unaires.length; i++){
                if((this.unaires[i][0] == true)&&(this.unaires[i][1] == true)&&(this.unaires[i][2] == true)) {
                    b = false;
                    this.bb = 1 ;
                    continue OUTER_LOOP ;
                }
            }

            //cas 2 :
            for(int i = 0 ; i < this.unaires.length; i++){
                if((this.unaires[i][0] == true)&&(this.unaires[i][1] == true)) {
                    cas_2(i,0 , 1 , 2);
                    //b = verfierContraiteRestantes();
                    continue OUTER_LOOP;
                }
                else if((this.unaires[i][0] == true)&&(this.unaires[i][2] == true)){
                    cas_2(i, 0 , 2 , 1 );
                   // b = verfierContraiteRestantes();
                    continue OUTER_LOOP;
                }
                else if((this.unaires[i][1] == true)&&(this.unaires[i][2] == true)){
                    cas_2(i, 1 , 2 , 0);
                   // b = verfierContraiteRestantes();
                    continue OUTER_LOOP;
                }
            }

            //cas 3 :
            for (int i = 0 ; i < this.unaires.length; i++){
                if(this.unaires[i][0] == true) {
                    cas_3(i, 0);
                   // b = verfierContraiteRestantes();
                    continue OUTER_LOOP;
                }
                else if(this.unaires[i][1] == true){
                    cas_3(i , 1);
                  //  b = verfierContraiteRestantes();
                    continue OUTER_LOOP;
                }
                else if(this.unaires[i][2] == true){
                    cas_3(i , 2);
                   // b = verfierContraiteRestantes();
                    continue OUTER_LOOP;
                }
            }


            //cas 4 :
            for (int i = 0 ; i < this.binaires.length ; i++){
                for (int j = 0 ; j < this.binaires.length ; j++){
                    for (int k = 0 ; k < 3 ; k++){
                        for (int  u = 0 ; u < 3 ; u++){
                            if (this.binaires[i][j][k][u]){
                                cas_4(i , j , k , u);
                            //    b = verfierContraiteRestantes();
                                continue OUTER_LOOP;
                            }

                        }
                    }
                }
            }

            b = false;

        }

        if (b == false && this.bb == 1){
            System.out.println("non coloriable");
        }else if (b == false)
            System.out.println("coloriable");
    }

    /**
     * Affichage des contraintes au crous de developpement
      */
    public void afficherContraintes(){
        System.out.println("errre");
    }
}
