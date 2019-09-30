package io.github.xpakx.micro.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class CustomDialect extends AbstractProcessorDialect 
{
  private static final String DIALECT_PREFIX = "msg";
  private static final int PRECEDENCE = 10000;
  
  public CustomDialect() 
  {
    super("Message Processor", DIALECT_PREFIX, PRECEDENCE);
  }


  public Set<IProcessor> getProcessors(final String dialectPrefix) 
  {
    final Set<IProcessor> processors = new HashSet<IProcessor>();
    processors.add(new PostMessageAttributeProcessor(dialectPrefix));
    processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
    return processors;
  }


}
