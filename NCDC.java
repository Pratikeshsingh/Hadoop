package org.myorg;
import java.io.IOException;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

public class NCDC extends Configured implements Tool 
{
	public static void main(String[] args) throws Exception
	{
		int res = ToolRunner.run(new NCDC(), args);
		System.exit(res);
	}
	public int run(String[] args) throws Exception
	{
		Job job = Job.getInstance(getConf(), "ncdc");
		job.setJarByClass(this.getClass());
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}
	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable>
	{
		private static IntWritable Temp = new IntWritable();
		private Text key=new Text();
		String line, month, temp,quality;
		int intTemp;
		final int MISSING=9999;
		public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException
		{
			line=lineText.toString();
			month=line.substring(19,21);
			quality = line.substring(92,93);
			if(line.charAt(87)=='+')
				temp=line.substring(88,92);
			else
				temp=line.substring(87,92);
			intTemp = Integer.parseInt(temp);
			if(intTemp!=MISSING && quality.matches("[01459]"))
				{
					
					Temp.set(intTemp);
					key.set(month);
					context.write(key,Temp);
				}

		}
	}
	public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable>
	{
		private IntWritable value = new IntWritable();
		int max;
		public void reduce(Text month, Iterable<IntWritable> counts, Context context) throws IOException, InterruptedException
		{
			max = -2147483647;
			for (IntWritable count:counts )
			{
				if(count.get()>=max)
				max = count.get();
			}
			value.set(max);
			context.write(month,value);
		} 
	}
}
