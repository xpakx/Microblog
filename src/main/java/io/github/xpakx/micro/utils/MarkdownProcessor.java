package io.github.xpakx.micro.utils;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class MarkdownProcessor
{
  private Parser markdownParser;
  private HtmlRenderer htmlRenderer;
  
  public MarkdownProcessor()
  {
    MutableDataSet options = new MutableDataSet();
    markdownParser = Parser.builder(options).build();
    htmlRenderer = HtmlRenderer.builder(options).build(); 
  }
  
  public String parseMarkdownToHTML(String messageToParse)
  {
    Node parsedMessage = markdownParser.parse(messageToParse);
    return htmlRenderer.render(parsedMessage);
  }
}
