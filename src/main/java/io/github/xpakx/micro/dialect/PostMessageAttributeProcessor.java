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

public class PostMessageAttributeProcessor extends AbstractAttributeTagProcessor 
{
  private static final String ATTR_NAME = "process";
  private static final int PRECEDENCE = 10000;
  
  MarkdownProcessor markdownProcessor;
  
  public PostMessageAttributeProcessor(final String dialectPrefix) 
  {
    super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE,        true);  
    markdownProcessor = new MarkdownProcessor();
  }
  
  protected void doProcess(final ITemplateContext context, final IProcessableElementTag tag, final AttributeName attributeName, final String attributeValue, final IElementTagStructureHandler structureHandler) 
  {
    final IEngineConfiguration configuration = context.getConfiguration();
    final IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
    final IStandardExpression expression = parser.parseExpression(context, attributeValue);
    final String message = (String) expression.execute(context);
    final String messageWithoutHTML = HtmlEscape.escapeHtml5(message);
    
    
    final String messageWithHTML = markdownProcessor.parseMarkdownToHTML(messageWithoutHTML);
    
    final String tagAddress =  RequestDataValueProcessorUtils.processUrl(context, "/tag/");
    final String userAddress =  RequestDataValueProcessorUtils.processUrl(context, "/user/");
    final String finalMessage = messageWithHTML
      .replaceAll("(\\s|\\A|>)#(\\w+)", "$1#<a href='"+tagAddress+"$2'>$2</a>")
      .replaceAll("(\\s|\\A|>)@(\\w+)", "$1@<a href='"+userAddress+"$2'>$2</a>");
      
    structureHandler.setBody(finalMessage, false);
  }


}
