package io.github.xpakx.micro.dialect;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;

import io.github.xpakx.micro.utils.MarkdownProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.github.xpakx.micro.service.UserService;

public class PostMessageAttributeProcessor extends AbstractAttributeTagProcessor 
{
  private static final String ATTR_NAME = "process";
  private static final int PRECEDENCE = 10000;
  
  MarkdownProcessor markdownProcessor;
  
  UserService userService;
  
  public PostMessageAttributeProcessor(final String dialectPrefix, UserService userService) 
  {
    super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE,        true);  
    markdownProcessor = new MarkdownProcessor();
    this.userService = userService;
  }
  
  protected void doProcess(final ITemplateContext context, final IProcessableElementTag tag, final AttributeName attributeName, final String attributeValue, final IElementTagStructureHandler structureHandler) 
  {
    final IEngineConfiguration configuration = context.getConfiguration();
    final IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
    final IStandardExpression expression = parser.parseExpression(context, attributeValue);
    final String message = (String) expression.execute(context);
    final String messageWithoutHTML = HtmlEscape.escapeHtml5(message)
      .replaceAll("(\\s|\\A)&gt;", "$1>");
    
    final String messageWithHTML = markdownProcessor.parseMarkdownToHTML(messageWithoutHTML);
        
    final String tagAddress =  RequestDataValueProcessorUtils.processUrl(context, "/tag/");
    final String userAddress =  RequestDataValueProcessorUtils.processUrl(context, "/user/");
    final String messageWithTags = messageWithHTML
      .replaceAll("(\\s|\\A|>)#(\\w+)", "$1#<a href='"+tagAddress+"$2'>$2</a>");
    final String finalMessage = replaceMentionsToLinksIfMentionedUsersExists(messageWithTags, userAddress);   
      
    structureHandler.setBody(finalMessage, false);
  }
  
  public String replaceMentionsToLinksIfMentionedUsersExists(String messageToParse, String userAddress)
  {
    Pattern pattern = Pattern.compile("(\\s|\\A|>)@(\\w+)");
    Matcher matcher = pattern.matcher(messageToParse);
    StringBuffer sb = new StringBuffer(messageToParse.length());
    
    while (matcher.find()) 
    {
      String username = matcher.group(2);
      if(userService.findByUsernameIgnoreCase(username).isPresent())
      {
        String linkToUser = matcher.group(1)+"@<a href='"+userAddress+matcher.group(2)+"'/posts>"+matcher.group(2)+"</a>";
        matcher.appendReplacement(sb, Matcher.quoteReplacement(linkToUser));
      }
    }
    matcher.appendTail(sb);
    return sb.toString();
  }


}
