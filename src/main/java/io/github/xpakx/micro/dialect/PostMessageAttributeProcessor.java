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

public class PostMessageAttributeProcessor extends AbstractAttributeTagProcessor 
{
  private static final String ATTR_NAME = "process";
  private static final int PRECEDENCE = 10000;
  
  public PostMessageAttributeProcessor(final String dialectPrefix) 
  {
    super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE,        true);   
  }

  protected void doProcess(final ITemplateContext context, final IProcessableElementTag tag, final AttributeName attributeName, final String attributeValue, final IElementTagStructureHandler structureHandler) 
  {
    final IEngineConfiguration configuration = context.getConfiguration();
    final IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
    final IStandardExpression expression = parser.parseExpression(context, attributeValue);
    final String message = (String) expression.execute(context);
    final String messageWithoutHTML = HtmlEscape.escapeHtml5(message);
    final String finalMessage = messageWithoutHTML
      .replace("\n", "<br />")
      .replaceAll("(\\s|\\A)#(\\w+)(\\s|\\z)", "$1#<a href=''>$2</a>$3")
      .replaceAll("(\\s|\\A)@(\\w+)(\\s|\\z|:)", "$1@<a href=''>$2</a>$3");
    structureHandler.setBody(finalMessage, false);
  }


}
