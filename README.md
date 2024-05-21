# README

## Ability to add more archiving methods

This project has been designed to make it easy to add additional archiving methods. It uses the
strategy design pattern to enable this. 
Below is a summary of design choices to facilitate the addition of more archiving methods:
- ArchivingMethodEnum: Currently only includes "ZIP", but additional enums are easy to add.
- Controller: The POST endpoint takes in an optional "method" parameter (parsed as ArchivingMethodEnum) 
that it passes to the archive service. If not included, this parameter defaults to ZIP.
- ArchiveService: Wraps an instance of ArchiverInterface and sets it to a specific archiving class based on the
archiving method enum passed to its constructor or setter method. 
- ArchiverInterface has a single method "compress". Any new archiving classes should implement this interface.

Thus, to add support for a new archiving method, 3 changes are necessary.
1. Add an enum to ArchivingMethodEnum.
2. Write a class implementing ArchiverInterface to support the new archiving method.
3. Add an additional case to the switch statement in ArchiveService's setter method.

## Face significant increase in request count

The biggest problem associated with an increase in request count is probably the exhaustion
of available threads to handle requests.
There are several ways to adapt the project to handle this:
1. Increase the size of the thread pool for handling requests. This is perhaps the
simplest solution and would work up to a point. However, threads do consume memory so
there is a level at which this strategy becomes impractical since machines have finite resources.

2.  Horizontal scaling (e.g. adding more containers). This is another obvious solution
and could theoretically work for an extremely high number of threads. The downside to this
strategy is that it incurs additional hosting costs. The advantage is that it requires no
refactoring since request processing is already stateless.

3. Asynchronous processing of requests. The request thread can hand the archiving job off to a work queue or similar structure
and close the connection with the client immediately. The client can then access their job, either by polling a unique URI or by receiving it in a webhook or similar method. By decoupling request handling from archiving, the server's throughput increases
dramatically. Furthermore, it gives more control over when and how archving jobs are processed, potentially allowing the use of fewer computing
resources or other cost saving measures. \
This approach represents a significant change in the way the application handles requests and would thus require significant refactoring.
Furthermore, it introduces added complexity on both the client and the server, since some structure has to be set up so that the 
client can access their resources independently of their initial connection with the server. Note also that files would have to be stored
somewhere, probably in an object storage service like S3, which would introduce additional costs and complexity.



## Handling large files

Large files introduce a unique set of challenges for processing, both because of their memory requirements and slow upload and processing times. Below are some ways that these issues could be addressed:

- To avoid using all the available memory, files above a certain size can be written to disk. The advantage of this approach is that it is extremely easy to do in Spring (adjust spring.servlet.multipart.file-size-threshold) and prevents the application from running out of RAM. However, if a large volume of requests came in, the disk might fill up, especially if the server is running on containers with limited disk space. Furthermore, disk I/O is very slow, adding to the already lengthy upload and processing time.

 -  To avoid storing files in memory at all, it might be possible to 
process the files in a stream, (i.e. as the data comes in). The downside to this approach is that some compression methods require
access to the whole file before they compress it, and thus they would be incompatible with this approach. The other downside to this
approach is that it would be incompatible with many of the other suggested improvements for this issue listed below, all of which require
access to the whole file or files. However, if done well, this approach might virtually eliminate many of the memory issues associated with large files.

 - If multiple files were received, it might be possible to process them in parallel, reducing the required processing time. This could be done either synchronously (client connection remains open) or asynchronously. Parallel processing would likely be relatively easy to implement but is not compatible with some archiving methods.

 - Asynchronous processing as discussed above would help alleviate the issues associated with slow processing, since the client's connection would not have to remain open while the file was processed.

 - A common feature available in many APIs that receive large files (e.g. Vimeo, Youtube) is the ability to resume failed or paused uploads. There are many implementations of this feature, including the open protocol "tus". Resumable uploads can potentially reduce the amount of
data uploaded to the server, and are especially important if it is likely that the client's connection is unstable. \
The downsides to implementing this feature include its complexity and requirement for file storage. Since this feature essentially forces asynchronous processing, all the benefits and downsides associated with that apply here as well.







