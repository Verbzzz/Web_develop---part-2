package main.java.com.hit.dao;

import com.google.gson.Gson;
import main.java.com.hit.algo.KMP;
import main.java.com.hit.dm.Game;
import main.java.com.hit.service.GameService; //////////////////////////

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameDaoImpl implements IDao<Game> {

    public enum GameKey {
        GameName,
        Genre,
        GameCompanyDevelop,
        GameStoreName,
        AddressStore,
    }

    public static void main(String[] args) {
        String GameName = "HEN_STORE";
        String Val = "GTA";
        String toUpdate = "GameName";
        GameService service = new GameService();

        GameDaoImpl.GameKey gameKey = Enum.valueOf(GameDaoImpl.GameKey.class, toUpdate);

        service.updateGame(GameName,gameKey,Val);
    }


    @Override
    public List<Game> getGame(String searchVal){

        Generator generator = new Generator();
        List<Game> gameList=new ArrayList<>();

        String Text;
        int result = 0;

        try {
            for(String id: generator.idSet) {
                Gson gson = new Gson();
                Reader reader = Files.newBufferedReader(Paths.get(id + ".json"));

                Game game = gson.fromJson(reader, Game.class);
                Text = game.toString();

                KMP kmp = new KMP(Text , searchVal);
                String txt = kmp.getTxt();
                String pat = kmp.getPat();
                result = kmp.Search(txt, pat);

                if (result == 1)
                    gameList.add(game);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gameList;
    }

    @Override
    public void saveGame(Game game) {
        new Generator().jsonBuilder(game);
    }

    @Override
    public void updateGame(String gameName, GameKey key, String updateVal) {

        Generator generator = new Generator();
        try {
            for(String id: generator.idSet) {
                Gson gson = new Gson();
                Reader reader = Files.newBufferedReader(Paths.get(id + ".json"));
                Game game = gson.fromJson(reader, Game.class);

                String name = game.getGameName();

                if (name != null && name.equals(gameName)){
                    switch (key){
                        case GameName: game.setGameName(updateVal);
                        break;
                        case Genre:game.setGenre(updateVal);
                        break;
                        case GameCompanyDevelop:game.setGameCompanyDevelop(updateVal);
                        break;
                        case GameStoreName:game.setGameStoreName(updateVal);
                        break;
                        case AddressStore:game.setAddressStore(updateVal);
                        break;
                        default:
                            break;
                    }
                    generator.jsonBuilder(game);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteGame(String gameName) {

        Generator generator = new Generator();

        try {
            for(String id: generator.idSet) {
                Gson gson = new Gson();
                Reader reader = Files.newBufferedReader(Paths.get(id + ".json"));
                Game game = gson.fromJson(reader, Game.class);

                String name = game.getGameName();

                if (name != null && name.equals(gameName)) {
                    try {
                        Files.delete(Paths.get(id + ".json"));
                        generator.idSet.remove(game.getUuid());
                        try (FileWriter fileWriter1 = new FileWriter("UUID.json")) {
                            new Gson().toJson(generator.idSet, fileWriter1);
                        }
                    } catch (IOException e){

                        e.printStackTrace();
                    }
                }
            }
            } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
