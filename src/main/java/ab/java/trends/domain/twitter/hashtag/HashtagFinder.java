package ab.java.trends.domain.twitter.hashtag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
public class HashtagFinder {
   private static final String HASHTAG_PATTERN_STR = "#[a-zA-Z0-9]{1,}";
   private static final Pattern HASHTAG_PATTERN = Pattern.compile(HASHTAG_PATTERN_STR);
   
   public static  Stream<String> findHashtags(String text) {
       if(StringUtils.isEmpty(text)) {
           return Stream.empty();
       }
       
       List<String> hashtags = new ArrayList<>();
       
       Matcher matcher = HASHTAG_PATTERN.matcher(text);
       
       while(matcher.find()) {
           hashtags.add(text.substring(matcher.start(), matcher.end()));
       }

       return hashtags.stream();
   }
   
        public static void main(String[] args) {
        System.out.println(Arrays.toString(HashtagFinder.findHashtags("Love me when I least deserve it because that is when I really need it. #ALDUB17thMonthsary\n" +
"@jophie30 @Danggoy56").toArray()));
    }
    
}
