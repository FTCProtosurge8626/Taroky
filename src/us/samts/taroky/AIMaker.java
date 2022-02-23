package us.samts.taroky;

public class AIMaker extends Table {
    int[] initialSeed;
    protected AIMaker() {
        super(0);
    }

    @Override
    public void message(String message) {}
    /*
    * The AI will be composed as follows:
    * Each AI will have inputs, as shown in the constructInputs() method in the AI class
    * These inputs will lead to 20 "hidden" neurons
    * These neurons will lead to 20 additional "hidden" neurons
    * These will lead to the 10 outputs
    *
    * Each output is what decision the AI makes when the Table requests an action. These include
    *   Shuffle pattern     (0,1,2,3 on repeat and for how long)
    *   Cut choice          (1,2,3,4,5,6,12,345)
    *   Prever choice       (boolean)
    *   Prever Talon        (Whether to keep the given Talon or swap)
    *   Discard             (Which cards to discard)
    *   Fleck               (boolean)
    *   Pagat               (boolean)
    *   Valat               (boolean)
    *   Determine Partner   (Whether to play by itself when it has the XIX and is pavenost)
    *   Lead                (Which card to lead)
    *   TakeTurn            (Which card to play on its turn)
    *
    * These choices will determine how "good" the AI is
    * The AI will each play 10,000 rounds, then the highest scoring AI will become the seed.
    * The seed will continue on, with the other AI being slight variations of it
    *
    * AIMaker will run this, and after each round will print the seed
    * This seed can be pasted into the initialSeed variable to start off the next generation if there is a pause between runs
    *
    * Neural Network:
    * INPUTS (A lot)
    * HIDDEN 20
    * HIDDEN 20
    * OUTPUTS 11
    *
    * Connections:
    * I>H1  =
    * H1>H2 = 400
    * H2>O  = 232
    * weightI[inputs][20]
    * weightsH[20][20]
    * weightsO[20][11]
    *
    * outputsI[20]
    * outputsH[20]
    *
    * */
}
