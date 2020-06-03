package AbohBOTGradle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * 
 * This is the main-class of the bot, where the bot is built using discords token system
 * bot extends listenerAdapter -> this is the eventListener for discord events such as messageReceivedEvents
 *      
 */

public class Bot extends ListenerAdapter {
	
	// the soundCollection that stores every sound and it's path
	static SoundCollection soundCollection;
	
	 
	public static void main(String[] args) throws LoginException, IOException, InterruptedException {
		
		
		// building the bot and adding the eventListener
		JDA bot = JDABuilder.createDefault("TOKEN").setActivity(Activity.listening("Russian Hardbass")).setAutoReconnect(true).build().awaitReady();	
		bot.setAutoReconnect(true);
		bot.addEventListener(new Bot());
	
		//initializing the sound collection
		soundCollection = new SoundCollection();
		
	}


  /**
   * GuildMessageReceived method to handle messageReceived events (user sends discord message in a discord server channel)
   *
   * @param event
   *        The guildMessageReceivedEvent of the sent message      
   *        
   */	
	
  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
     // get message, author and content and save it for later
	  Message message = event.getMessage();
      User author = message.getAuthor();
      String content = message.getContentRaw();
     
    
      // Ignore message if bot
      if (author.isBot())
          return;

      /*
       * the specific sound command (e.g. someone types !airhorn)
       * check if user input matches entry of soundcollection (just loop through it)
       * if yes, pass to onEchoCommand method (pass event and path of sound)
       *  
       */
      
      for (int i = 0; i <soundCollection.getYtsounds().size(); i++) {
    	  if (content.contentEquals("!" +soundCollection.getYtsounds().get(i).getName()))
    	  {
    		String path = System.getProperty("user.dir") +"/Sounds/"+ soundCollection.getYtsounds().get(i).getName() + ".mp3";
    		  onEchoCommand(event,path);
    	  }
      }
      
      /*
       * !play command 
       *  plays audio sources from youtube or other sources
       * pass event and url to onEchoCommand method 
       */
      
      if (content.startsWith("!play")) {
    	  String url = content.substring(6);
    	  onEchoCommand(event, url);
    	    
      }
      
      /*
       * !skip command
       * pass url and 'skip' identifier to onEchoCommand (this will stop the bot playing sounds)     * 
       */
      
      if(content.contentEquals("!stop")) {
    	  onEchoCommand(event, "skip");
    	  
    	  
      }
      
      
      /*
       * 
       * !add command
       * command to add new sounds (mp3 files)
       * try getting attachment of message -> pass to saveLocally method
       * 
       */
      if (content.contentEquals("!add")) {
    	  try {
			saveLocally(message.getAttachments().get(0));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
      }
      
      
      /*
       * !random command
       * plays a random sound
       * gets a random sound from the sound collection and passes it to onEchoCommand (event and path)
       */
      
      if (content.contentEquals("!random")) {
      		
    	 int rng = (int) (Math.random() * soundCollection.getYtsounds().size());
    	 onEchoCommand(event, soundCollection.getYtsounds().get(rng).getPath());
    	  
      }
  
      /*
       * !soundlist command
       * user receives name of every sound in the sound collection
       * just loop through every sound name in the sound collection and send it 
       */
      if (content.contentEquals("!soundlist")) {
    	 
    	  author.openPrivateChannel().flatMap(channel -> channel.sendMessage(soundCollection.createSoundlist()))
	         .queue();
    	  	  
      }
      
      
      /*
       * !activity command
       * change the activity of the bot (fluff feature)
       * e.g. bot watches 'MemeTV'
       * 
       */
      
      if(content.startsWith("!activity")) {

    	  String activity = content.substring(9);
    	  
    	  if (activity.startsWith(" watching")) {
    		  event.getJDA().getPresence().setActivity(Activity.of(ActivityType.WATCHING, activity.substring(9)));
    		  
    	  }
    	  
    	  if (activity.startsWith(" playing")) {
    		  event.getJDA().getPresence().setActivity(Activity.of(ActivityType.STREAMING, activity.substring(9)));
    		 
    	  }
    	  
    	  if (activity.startsWith(" listening")) {
    		  event.getJDA().getPresence().setActivity(Activity.of(ActivityType.LISTENING, activity.substring(10)));
    	  }
    	  
    	  
      }
  
  
  }
    
  /**
   * Handle command without arguments.
   *
   * @param event
   *        The event for this command
   * @param path
   * path to the sound file       
   *        
   */
  private void onEchoCommand(GuildMessageReceivedEvent event, String path)
 
  {
	  
	  
	  
      Member member = event.getMember();                              // Member is the context of the user for the specific guild, containing voice state and roles
      GuildVoiceState voiceState = member.getVoiceState();            // Check the current voice state of the user
      VoiceChannel channel = voiceState.getChannel();
     
      																	// Use the channel the user is currently connected to
     
      //this only happens when user entered skip
      //pass channel and skip identifier to PlayerManager (which will execute the skip)
      
      if(path.equalsIgnoreCase("skip")) {
    	  PlayerManager manager = PlayerManager.getInstance();
    	  manager.loadAndPlay(event.getChannel(), path);
    	  
    	  
    	  
      }
      
      //if bot is not connected to a voice channel
     
      if (channel != null)
    	  
      {
    	  // Join the channel of the user
          connectTo(channel);   
         

          // instruct playerManager to play the sound file via loadAndPlay (pass channel and path to sound file)
          PlayerManager manager = PlayerManager.getInstance();
  		  manager.loadAndPlay(event.getChannel(), path);
         
      
        
          
      }
      else
      {
          onUnknownChannel(event.getChannel(), "your voice channel"); // Tell the user about our failure
      }
  }



  /**
   * Inform user about successful connection.
   *
   * @param channel
   *        The voice channel we connected to
   * @param textChannel
   *        The text channel to send the message in
   */
  private void onConnecting(VoiceChannel channel, TextChannel textChannel)
  {
     
	  // you can use this to send a success message after bot connected to voice channel
	  
  }

  /**
   * The channel to connect to is not known to us.
   *
   * @param channel
   *        The message channel (text channel abstraction) to send failure information to
   * @param comment
   *        The information of this channel
   */
  private void onUnknownChannel(MessageChannel channel, String comment)
  {
      channel.sendMessage("Unable to connect to ``" + comment + "``, no such channel!").queue(); 
  }

  /**
   * Connect to requested channel and start echo handler
   *
   * @param channel
   *        The channel to connect to
   */
  private void connectTo(VoiceChannel channel)
  {
      Guild guild = channel.getGuild();
      // Get an audio manager for this guild, this will be created upon first use for each guild
      AudioManager audioManager = guild.getAudioManager();
      // Create our Send/Receive handler for the audio connection
      PlayerManager pm = PlayerManager.getInstance();
    

      // The order of the following instructions does not matter!

      // Set the sending handler to our echo system
      audioManager.setSendingHandler(pm.getGuildMusicManager(guild).getSendHandler());
      // Set the receiving handler to the same echo system, otherwise we can't echo anything
      //   audioManager.setReceivingHandler(handler);
      // Connect to the voice channel
      audioManager.openAudioConnection(channel);
  } 
  
  	
 
  /**
   * save mp3 file locally to working directory
   *
   * @param attachment
   *        the attachment of the discord message (mp3 file in this case)
   *
   */

  public void saveLocally(Message.Attachment attachment) throws IOException, InterruptedException
  {
	  
	 String filename = attachment.getFileName();
	 String sourcepath = System.getProperty("user.dir");
	 // temp file to check if sound file already exists
	 File check_existing = new File((sourcepath + "/Sounds" + "/" + filename));
	
	 //if a sound already exists delete it (basically overwriting)
	 if(check_existing.exists()) {
		 Files.delete(check_existing.toPath());
		
	 }
	 
	  //download attachment and print result on console
      attachment.downloadToFile()
      .thenAccept(file -> System.out.println("Saved attachment to " + file.getName()))
          .exceptionally(t ->
          { // handle failure
              t.printStackTrace();
              return null;
          });
      
     // otherwise it throws NPEs... -__-
     Thread.sleep(1000);
     
     //move sound to correct location
	 moveSound(filename, sourcepath);
	
	 //update sound collection
	 soundCollection.updateFile();
	 soundCollection.updateCollection();
	 
	 
  }
  
  /**
   * move sound file to workingDir/sounds using Files class of JavaIO
   *
   *
   * @param filename
   *        name of the sound file
   * 
   * @param sourcepath
   * path to workingDir (location of the file that is about to be moved to the correct location)
   */
  
  
  public void moveSound(String filename, String sourcepath) throws IOException {
	  
	  //create two files, source and target
	  File source = new File(((sourcepath + "/"+filename)));
      File target = new File(((sourcepath + "/Sounds" + "/" + filename)));
      //copy from source to target 
      Files.copy(source.toPath(), target.toPath());
      Files.delete(source.toPath());
      
  }
  
  
}
